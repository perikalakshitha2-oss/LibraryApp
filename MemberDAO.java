package com.library.dao;

import com.library.model.Member;
import com.library.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data-Access Object for the {@code members} table.
 */
public class MemberDAO {

    // ── CREATE ───────────────────────────────────────────────

    public boolean addMember(Member member) throws SQLException {
        String sql = "INSERT INTO members (name, email, phone, address) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, member.getName());
            ps.setString(2, member.getEmail());
            ps.setString(3, member.getPhone());
            ps.setString(4, member.getAddress());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) member.setMemberId(keys.getInt(1));
                return true;
            }
            return false;
        }
    }

    // ── READ ─────────────────────────────────────────────────

    public List<Member> getAllMembers() throws SQLException {
        List<Member> list = new ArrayList<>();
        String sql = "SELECT * FROM members ORDER BY name";

        try (Statement st = DBConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public Member getMemberById(int memberId) throws SQLException {
        String sql = "SELECT * FROM members WHERE member_id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, memberId);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? mapRow(rs) : null;
        }
    }

    public List<Member> searchMembers(String keyword) throws SQLException {
        List<Member> list = new ArrayList<>();
        String sql  = "SELECT * FROM members WHERE name LIKE ? OR email LIKE ? OR phone LIKE ?";
        String kw   = "%" + keyword + "%";

        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, kw);
            ps.setString(2, kw);
            ps.setString(3, kw);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    // ── UPDATE ───────────────────────────────────────────────

    public boolean updateMember(Member member) throws SQLException {
        String sql = "UPDATE members SET name=?, email=?, phone=?, address=?, is_active=? " +
                     "WHERE member_id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString (1, member.getName());
            ps.setString (2, member.getEmail());
            ps.setString (3, member.getPhone());
            ps.setString (4, member.getAddress());
            ps.setBoolean(5, member.isActive());
            ps.setInt    (6, member.getMemberId());
            return ps.executeUpdate() > 0;
        }
    }

    // ── DELETE ───────────────────────────────────────────────

    public boolean deleteMember(int memberId) throws SQLException {
        String sql = "DELETE FROM members WHERE member_id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, memberId);
            return ps.executeUpdate() > 0;
        }
    }

    // ── Private helper ───────────────────────────────────────

    private Member mapRow(ResultSet rs) throws SQLException {
        Member m = new Member();
        m.setMemberId  (rs.getInt    ("member_id"));
        m.setName      (rs.getString ("name"));
        m.setEmail     (rs.getString ("email"));
        m.setPhone     (rs.getString ("phone"));
        m.setAddress   (rs.getString ("address"));
        m.setJoinedDate(rs.getDate   ("joined_date").toLocalDate());
        m.setActive    (rs.getBoolean("is_active"));
        return m;
    }
}
