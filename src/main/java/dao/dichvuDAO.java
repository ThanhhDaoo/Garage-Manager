package dao;

import model.dichvuVIEW;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class dichvuDAO {
    public List<dichvuVIEW> getAll() {
        List<dichvuVIEW> list = new ArrayList<>();

        try {
            Connection conn = DriverManager.getConnection("jdbc:sqlite:garage.db");

            String sql = "SELECT * FROM v_bang_gia_dich_vu";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                dichvuVIEW dv = new dichvuVIEW();

                dv.setMaDV(rs.getInt("ma_dv"));
                dv.setTen(rs.getString("ten"));
                dv.setDonVi(rs.getString("don_vi"));
                dv.setGiaSedan(rs.getDouble("gia_sedan"));
                dv.setGiaSUV(rs.getDouble("gia_suv"));

                list.add(dv);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
