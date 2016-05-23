package db;

import data.SearchKeyInfo;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by guanxiaoda on 5/20/16.
 *
 */
public class ORM {



    public static List<SearchKeyInfo> searchKeyInfoMapRow(SqlRowSet rs) throws SQLException {
        List<SearchKeyInfo> skis = new ArrayList<SearchKeyInfo>();
        while(rs.next()) {
            SearchKeyInfo ski = new SearchKeyInfo();
            ski.setDbOriginalId(rs.getInt(1));
            ski.setCategory_code(rs.getInt(2));
            ski.setKeyword(rs.getString(3));
            ski.setSite_id(rs.getString(4));
            ski.setSite_name(rs.getString(5));
            skis.add(ski);
        }
        return skis;
    }
}
