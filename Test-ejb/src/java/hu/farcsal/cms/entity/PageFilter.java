package hu.farcsal.cms.entity;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author zoli
 */
@Entity
@Table(name="page-filters")
@IdClass(PageFilter.Key.class)
public class PageFilter implements Serializable {
    
    @Id
    @ManyToOne
    @JoinColumn(name="page-id")
    private Page page;
    
    @Id
    @ManyToOne
    @JoinColumn(name="site-id")
    private Site site;
    
    @Column(name="single", nullable = false)
    private boolean single;
    
    public static class Key implements Serializable {
        
        private Long page;
        
        private Long site;

        protected Key() {
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) return true;
            if (!(object instanceof Key)) return false;
            Key other = (Key) object;
            return Objects.equals(page, other.page) && Objects.equals(site, other.site);
        }
        
        @Override
        public int hashCode() {
            if (page == null || site == null) return 0;
            return 31 * (31 + page.hashCode()) + site.hashCode();
        }
        
    }

    protected PageFilter() {
    }

    public PageFilter(Page page, Site site) {
        this(page, site, false);
    }
    
    public PageFilter(Page page, Site site, boolean single) {
        this.page = page;
        this.site = site;
        this.single = single;
    }

    public Page getPage() {
        return page;
    }

    public Site getSite() {
        return site;
    }

    public boolean isSingle() {
        return single;
    }

    public void setSingle(boolean single) {
        this.single = single;
    }
    
    public static boolean isPageFiltered(List<PageFilter> filters, Site site) {
        if (filters != null) {
            for (PageFilter filter : filters) {
                if (filter == null || filter.getPage() == null || filter.getSite() == null) continue;
                boolean sameSite = filter.getSite().equals(site);
                return filter.isSingle() ? !sameSite : sameSite;
            }
        }
        return false;
    }
    
}
