package hu.farcsal.cms.entity;

import hu.farcsal.cms.entity.spec.Helpers;
import java.io.Serializable;
import java.util.ArrayList;
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
    
    @Column(name="view-path-generated", nullable = false)
    private boolean viewPathGenerated;
    
    @Column(name="action-inherited", nullable = false)
    private boolean actionInherited;
    
    @Column(name="parameter-incremented", nullable = false)
    private boolean parameterIncremented;
    
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
        if (isNameInvalid()) {
            return true;
        }
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

    public boolean isNameInvalid() {
        return isViewPathGenerated() && getViewPath() == null;
    }
    
    public boolean isViewPathGenerated() {
        return viewPathGenerated;
    }

    public void setViewPathGenerated(boolean viewPathGenerated) {
        this.viewPathGenerated = viewPathGenerated;
    }

    public boolean isActionInherited() {
        return actionInherited;
    }

    public void setActionInherited(boolean actionInherited) {
        this.actionInherited = actionInherited;
    }

    public boolean isParameterIncremented() {
        return parameterIncremented;
    }

    public void setParameterIncremented(boolean parameterIncremented) {
        this.parameterIncremented = parameterIncremented;
    }
    
    public String getViewPath() {
        return getViewPath(false);
    }
    
    public String getViewPath(boolean withDir) {
        String root = withDir ? getFacesDir() : null;
        if (viewPath == null) return null;
        String view = viewPath.trim();
        if (view.isEmpty()) return null;
        if (root != null && !view.startsWith("/")) return root + (root.endsWith("/") ? "" : "/") + view;
        return view;
    }
    
    public void setViewPath(String viewPath) {
        this.viewPath = viewPath;
    }
    
    public String getRealViewPath(boolean withDir) {
        try {
            return Helpers.pageHelper.getRealViewPath(this, withDir);
        }
        catch (Exception ex) {
            return getViewPath(withDir);
        }
    }
    
    private String getFacesDir() {
        try {
            return Helpers.pageHelper.getFacesDir();
        }
        catch (Exception ex) {
            return null;
        }
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
    
    public boolean isParameterRequired(boolean all, boolean allowIncrementedParams) {
        if (all) {
            for (Page p : getWay(false)) {
                if (p.isParameterRequired(this, allowIncrementedParams)) return true;
            }
            return false;
        }
        else {
            return isParameterRequired(allowIncrementedParams);
        }
    }
    
    public boolean isParameterRequired(boolean allowIncrementedParams) {
        return isParameterRequired(this, allowIncrementedParams);
    }
    
    private boolean isParameterRequired(Page page, boolean allowIncrementedParams) {
        if (this == page && isParameterIncremented() && allowIncrementedParams) return false;
        return getParameters() != null && !getParameters().isEmpty();
    }
    
    public static PageMapping findPageMapping(Page page, String language, String defLanguage, boolean skipParam, boolean allowIncrementedParams) {
        if (page == null || (skipParam && page.isParameterRequired(true, allowIncrementedParams)) || language == null) return null;
        List<PageMapping> mappings = page.getMappings();
        if (mappings == null || mappings.isEmpty()) return null;
//        PageMapping pm = mappings.get(0); // never returns null but mixes languages
        PageMapping pm = null; // the path can be broken
        for (PageMapping mapping : mappings) {
            if (mapping == null || mapping.getLanguage() == null) continue;
            if (defLanguage != null && defLanguage.equalsIgnoreCase(mapping.getLanguage().getCode())) {
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
