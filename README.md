# Fake Network Plugin Documentation

This documentation provides an overview of various systems and features implemented in the Hypixel Network plugin.

## Report System

The report system allows players to report other players for various reasons. Reports are stored in a MySQL database and can be managed by staff members.

- The `/report <player> <reason>` command allows players to report other players.
- The `/listreports` command provides a list of all reports along with their details.
- Staff members can use the `/acceptreport <reportId>` and `/denyreport <reportId>` commands to manage reports.
- Accepted and denied reports are moved to separate tables in the MySQL database.

## Parties System

The parties system enables players to create and manage parties for group gameplay.

- The `/party create` command allows players to create a party.
- The `/party invite <player>` command invites another player to the party.
- Players can accept or decline party invitations using the `/party accept` and `/party decline` commands.

## Guild System

The guild system allows players to create and join guilds, fostering community engagement.

- The `/guild create <name>` command allows players to create a guild.
- The `/guild invite <player>` command invites another player to the guild.
- Players can accept or decline guild invitations using the `/guild accept` and `/guild decline` commands.

## Storage using MySQL and HashMaps

The plugin utilizes both MySQL and HashMaps for efficient data storage and retrieval.

- MySQL is used to store long-term data, such as reports, accepted reports, denied reports, and player levels.
- HashMaps are used for quick in-memory access to frequently accessed data, improving performance.

## Usage of SimpleAPI

The Hypixel Network plugin makes use of the [SimpleAPI](https://github.com/NatesHonor/SimpleAPI) library to streamline development and enhance functionality.

- The SimpleAPI provides various utilities and helper methods for common tasks, such as database connections and messaging.
- It simplifies the integration of external APIs and enhances the plugin's modularity.

For more detailed information about specific commands, configurations, and usage instructions, refer to the respective sections in the plugin's source code.

Feel free to contribute, report issues, or suggest improvements by creating pull requests on the GitHub repository.

## Contributors

- [NatesHonor](https://github.com/NatesHonor) - Plugin Developer
