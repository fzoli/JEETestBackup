package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

/**
 *
 * @author zoli
 */
@Entity
@Table(name="pages")
@DiscriminatorValue("page")
public class Page extends Node<Page, PageMapping> {
    
    @Embedded
    @ElementCollection
    @OrderColumn(name="index", nullable = false)
    @CollectionTable(
        name="page-params",
        joinColumns=@JoinColumn(name="page-id", nullable = false)
    )
    private List<Parameter> parameters = new ArrayList<>();
    
    @OneToMany(mappedBy = "page")
    private List<PageFilter> pageFilters = new ArrayList<>();
    
    @Column(name="view-path", nullable=true)
    private String viewPath;
    
    @Column(name="site-dependent", nullable=false)
    private boolean siteDependent;
    
    @Column(name="action")
    private String action;
    
    @Column(name="action-inherited", nullable = false)
    private boolean actionInherited;
    
    @Embeddable
    public static class Parameter implements Serializable {
        
        @Column(name="name")
        private String name;
        
        @Column(name="bean-variable")
        private String beanVariable;
        
        @Column(name="validator")
        private String validator;

        protected Parameter() {
            this(null);
        }
        
        public Parameter(String name) {
            this(name, null);
        }
        
        public Parameter(String name, String beanVariable) {
            this(name, beanVariable, null);
        }
        
        private Parameter(String name, String beanVariable, String validator) {
            this.name = name;
            this.beanVariable = beanVariable;
            this.validator = validator;
        }
        
        public boolean isInvalid(Page owner) {
            return getValue() == null || isNameDuplicated(owner);
        }
        
        public boolean isNameDuplicated(Page owner) {
            int count = 0;
            if (getName() != null) for (Page p : owner.getWay(false)) {
                for (Parameter param : p.getParameters()) {
                    if (Objects.equals(getName(), param.getName())) {
                        if (++count > 1) return true;
                    }
                }
            }
            return false;
        }
        
        public String getValue() {
            if (name == null && beanVariable == null) return null;
            String value = name == null ? "" : name;
            if (name != null && beanVariable != null) value += " : ";
            if (beanVariable != null) value += beanVariable;
            if (value.trim().isEmpty()) return null;
            return value;
        }
        
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getBeanVariable() {
            return beanVariable;
        }

        public void setBeanVariable(String beanVariable) {
            this.beanVariable = beanVariable;
        }

        public String getValidator() {
            return validator;
        }

        public void setValidator(String validator) {
            this.validator = validator;
        }
        
    }
    
    public Page() {
        this((String) null);
    }
    
    public Page(String viewPath) {
        this((Page) null, viewPath);
    }
    
    public Page(Page parent, String viewPath) {
        this(parent, viewPath, false);
    }
    
    public Page(Page parent, String viewPath, boolean siteDependent) {
        super(parent);
        this.viewPath = viewPath;
        this.siteDependent = siteDependent;
    }
    
    protected Page(List<Page> children) {
        super(children);
    }

    public int getLevel(boolean withParams) {
        List<Page> pages = getWay(false);
        if (!withParams) return pages.size();
        int params = 0;
        for (Page p : pages) {
            params += p.getParameters().size();
        }
        return pages.size() + params;
    }
    
    public List<Parameter> getParameters() {
        return parameters;
    }

    public List<String> getParameterNames(boolean asValue) {
        if (parameters != null) {
            List<String> params = new ArrayList<>();
            for (Parameter param : parameters) {
                params.add(param == null ? null : (asValue ? param.getValue() : param.getName()));
            }
            return params;
        }
        return null;
    }

    @Override
    public boolean isDisabled() {
        if (parameters != null) {
            for (Parameter param : parameters) {
                if (param == null || param.isInvalid(this)) return true;
            }
        }
        return super.isDisabled();
    }
    
    public List<PageFilter> getPageFilters() {
        return pageFilters;
    }

    public boolean isSiteDependent() {
        return siteDependent;
    }
    
    public void setSiteDependent(boolean siteDependent) {
        this.siteDependent = siteDependent;
    }
    
    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public boolean isActionInherited() {
        return actionInherited;
    }

    public void setActionInherited(boolean actionInherited) {
        this.actionInherited = actionInherited;
    }
    
    public String getViewPath() {
        return getViewPath(null);
    }
    
    public String getViewPath(String root) {
        if (viewPath == null) return null;
        String view = viewPath.trim();
        if (view.isEmpty()) return null;
        if (root != null && !view.startsWith("/")) return root + (root.endsWith("/") ? "" : "/") + view;
        return view;
    }
    
    public void setViewPath(String viewPath) {
        this.viewPath = viewPath;
    }
    
    public List<String> getActions() {
        List<String> l = new ArrayList<>();
        for (Page p : getWay(true)) {
            String a = p.getAction();
            if (p == this || p.isActionInherited()) {
                if (a != null && !a.trim().isEmpty()) l.add(a);
            }
        }
        return l;
    }
    
    public List<Page> getWay(boolean fromRoot) {
        Page node = this;
        List<Page> way = new ArrayList<>();
        while (!node.isRoot()) {
            way.add(node);
            node = node.getParent();
        }
        way.add(node);
        if (fromRoot) Collections.reverse(way);
        return way;
    }
    
//    public boolean isParametersValid(List<String> paramValues, boolean strict) {
//        List<String> paramNames = getParameterNames(true);
//        boolean params = paramNames != null && !paramNames.isEmpty();
//        return (!params && !strict) || (!params && (paramValues == null || paramValues.isEmpty())) || (params && paramValues != null && paramValues.size() == paramNames.size());
//    }
    
    public boolean isParameterRequired(boolean all) {
        if (all) {
            for (Page p : getWay(false)) {
                if (p.getParameters() != null && !p.getParameters().isEmpty()) return true;
            }
            return false;
        }
        else {
            return getParameters() != null && !getParameters().isEmpty();
        }
    }
    
    public static PageMapping findPageMapping(Page page, String language, boolean skipParam) {
        if (page == null || (skipParam && page.isParameterRequired(true)) || language == null) return null;
        List<PageMapping> mappings = page.getMappings();
        if (mappings == null || mappings.isEmpty()) return null;
        PageMapping pm = mappings.get(0);
        for (PageMapping mapping : mappings) {
            if (mapping == null || mapping.getLanguage() == null) continue;
            if ("en".equalsIgnoreCase(mapping.getLanguage().getCode())) {
                pm = mapping;
            }
            if (language.equalsIgnoreCase(mapping.getLanguage().getCode())) {
                pm = mapping;
                break;
            }
        }
        return pm;
    }
    
}
