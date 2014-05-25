package entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
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
    
    @ElementCollection
    @Column(name="name", nullable = false)
    @OrderColumn(name="index", nullable = false)
    @CollectionTable(
        name="page-params",
        joinColumns=@JoinColumn(name="page-id", nullable = false)
    )
    private List<String> parameters = new ArrayList<>();
    
    @OneToMany(mappedBy = "page")
    private List<PageFilter> pageFilters = new ArrayList<>();
    
    @Column(name="view-path", nullable=false)
    private String viewPath;
    
    @Column(name="site-dependent", nullable=false)
    private boolean siteDependent;
    
    protected Page() {
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

    public List<String> getParameters() {
        return parameters;
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
    
    public boolean isParametersValid(List<String> paramValues, boolean strict) {
        List<String> paramNames = getParameters();
        boolean params = paramNames != null && !paramNames.isEmpty();
        return (!params && !strict) || (!params && (paramValues == null || paramValues.isEmpty())) || (params && paramValues != null && paramValues.size() == paramNames.size());
    }
    
    public static PageMapping findPageMapping(Page page, String language, boolean skipParam) {
        if (page == null || (skipParam && !page.getParameters().isEmpty()) || language == null) return null;
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
