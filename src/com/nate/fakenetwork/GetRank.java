package com.nate.fakenetwork;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GetRank {
    private final Core plugin;

    public GetRank(Core core) {
        this.plugin = core;
    }

    public String getRank(String playerName) {
        try {
            PreparedStatement statement = this.plugin.getConnection()
                    .prepareStatement("SELECT rankname FROM users WHERE username = ?");
            statement.setString(1, playerName);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String rankName = resultSet.getString("rankname");
                resultSet.close();
                statement.close();
                return rankName;
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}