package com.nate.fakenetwork.utils.storage.mysql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.nate.fakenetwork.Core;

public class Playtime {
    Core core = Core.getInstance();

    public long getTotalPlaytime(String playerName) {
        String query = "SELECT total_playtime FROM users WHERE player_name = ?";

        try {
            PreparedStatement statement = core.getConnection().prepareStatement(query);

            statement.setString(1, playerName);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getLong("total_playtime");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public void updateTotalPlaytime(String playerName, long additionalPlaytimeMillis) {
        String query = "UPDATE players SET total_playtime = total_playtime + ? WHERE player_name = ?";
        try {
            PreparedStatement statement = core.getConnection().prepareStatement(query);

            statement.setLong(1, additionalPlaytimeMillis);
            statement.setString(2, playerName);

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
