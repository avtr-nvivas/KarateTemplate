package helpers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.minidev.json.JSONObject;

public class DbHandler {

    private static final String CONNECTION_URL =
            "jdbc:sqlserver://192.168.179.50:1433;" +
            "databaseName=Pubs;" +
            "user=pubs_user;" +
            "password=PubsUser@123;" +
            "encrypt=false;trustServerCertificate=true";

    public static void addNewJobWithName(String jobName, int min_lvl, int max_lvl) {

        String sql = "INSERT INTO jobs (job_desc, min_lvl, max_lvl) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(CONNECTION_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, jobName);
            ps.setInt(2, min_lvl);
            ps.setInt(3, max_lvl);

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error inserting job", e);
        }
    }

    public static JSONObject getMinAndMaxLevelsForJob(String jobName) {

        JSONObject json = new JSONObject();

        String sql = "SELECT job_desc, min_lvl, max_lvl FROM jobs WHERE job_desc = ?";

        try (Connection conn = DriverManager.getConnection(CONNECTION_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, jobName);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    json.put("job_desc", rs.getString("job_desc"));
                    json.put("min_lvl", rs.getInt("min_lvl"));
                    json.put("max_lvl", rs.getInt("max_lvl"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error querying job levels", e);
        }

        return json;
    }
    
}
