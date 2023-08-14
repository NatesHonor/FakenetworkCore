package com.nate.fakenetwork.utils.storage.mysql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import com.nate.fakenetwork.Core;

public class Levels {
    Core core = Core.getInstance();

    public int getLevel(UUID playerUUID) {
        try {
            PreparedStatement statement = core.getConnection()
                    .prepareStatement("SELECT level FROM levels WHERE playeruuid = ?");
            statement.setString(1, playerUUID.toString());
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int level = resultSet.getInt("level");
                resultSet.close();
                statement.close();
                return level;
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1;
    }

    public void setLevel(UUID playerUUID, int level) {
        try {
            PreparedStatement statement = core.getConnection()
                    .prepareStatement("UPDATE levels SET level = ? WHERE playeruuid = ?");
            statement.setInt(1, level);
            statement.setString(2, playerUUID.toString());
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void savePlayerLevel(UUID playerUUID, int level, int exp) {
        try {
            PreparedStatement checkStatement = core.getConnection().prepareStatement(
                    "SELECT playeruuid FROM levels WHERE playeruuid = ?");
            checkStatement.setString(1, playerUUID.toString());
            if (checkStatement.executeQuery().next()) {
                PreparedStatement updateStatement = core.getConnection().prepareStatement(
                        "UPDATE levels SET level = ?, exp = ? WHERE playeruuid = ?");
                updateStatement.setInt(1, level);
                updateStatement.setInt(2, exp);
                updateStatement.setString(3, playerUUID.toString());
                updateStatement.executeUpdate();
                updateStatement.close();
            } else {
                PreparedStatement insertStatement = core.getConnection().prepareStatement(
                        "INSERT INTO levels (playeruuid, level, exp) VALUES (?, ?, ?)");
                insertStatement.setString(1, playerUUID.toString());
                insertStatement.setInt(2, level);
                insertStatement.setInt(3, exp);
                insertStatement.executeUpdate();
                insertStatement.close();
            }
            checkStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addPlayerToLevelTable(UUID playerUUID, int level, int eventLevel) {
        try {
            PreparedStatement checkStatement = core.getConnection().prepareStatement(
                    "SELECT playeruuid FROM levels WHERE playeruuid = ?");
            checkStatement.setString(1, playerUUID.toString());
            ResultSet resultSet = checkStatement.executeQuery();

            if (resultSet.next()) {
                resultSet.close();
                checkStatement.close();
                return;
            }

            resultSet.close();
            checkStatement.close();

            PreparedStatement insertStatement = core.getConnection().prepareStatement(
                    "INSERT INTO levels (playeruuid, level, eventlevel) VALUES (?, ?, ?)");
            insertStatement.setString(1, playerUUID.toString());
            insertStatement.setInt(2, level);
            insertStatement.setInt(3, eventLevel);
            insertStatement.executeUpdate();
            insertStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getPlayerExp(UUID playerUUID) {
        try {
            PreparedStatement statement = core.getConnection()
                    .prepareStatement("SELECT exp FROM levels WHERE playeruuid = ?");
            statement.setString(1, playerUUID.toString());
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int exp = resultSet.getInt("exp");
                resultSet.close();
                statement.close();
                return exp;
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void setPlayerExp(UUID playerUUID, int exp) {
        try {
            PreparedStatement statement = core.getConnection()
                    .prepareStatement("UPDATE levels SET exp = ? WHERE playeruuid = ?");
            statement.setInt(1, exp);
            statement.setString(2, playerUUID.toString());
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
