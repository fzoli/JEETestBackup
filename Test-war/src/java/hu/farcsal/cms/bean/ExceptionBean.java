package hu.farcsal.cms.bean;

import java.sql.SQLException;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

@ManagedBean
@RequestScoped
public class ExceptionBean {

    public void throwRuntimeException() {
        throw new RuntimeException("peek-a-boo");
    }

    public void throwSQLException() throws SQLException {
        throw new SQLException("DB fail");
    }

}
