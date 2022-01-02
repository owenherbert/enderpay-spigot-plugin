# Enderpay Spigot Plugin
The Enderpay plugin allows Enderpay to communicate with your Minecraft server.

## Required Plugins
The Enderpay plugin requires you to have the Vault plugin installed on your server.
* Vault - Vault allows the Enderpay plugin to sync the ranks of your players with Enderpay, this is used in package rank restriction bypasses.

## Available Commands
| Command                                | Description                                                                                                                                                                        |
|----------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| /enderpay-help                         | This command shows the Enderpay help message to the player that sends the command.                                                                                                 |
| /enderpay-setup <API-KEY> <API-SECRET> | This command sets up the Enderpay Minecraft plugin using the API-KEY and API-SECRET that you provide. This command will edit the plugins configuration file for you.               |
| /enderpay-force                        | This command will force the Enderpay Minecraft plugin to check for new commands from your store. Normally the Enderpay Minecraft plugin checks for new commands every 1-4 minutes. |
| /enderpay-sync                         | This command will force the Enderpay Minecraft plugin to get the latest inventory data from your store. Normally the Enderpay plugin only does this when the plugin starts.        |
| /buy                                   | This command will open up the buy GUI where players can view packages that are for sale.                                                                                           |

## Using Placeholders
![Enderpay Placeholders](https://i.imgur.com/V8es9BQ.jpg)
Enderpay provides several placeholders which you can use on your Minecraft server. See the information below for more detail. If you wish to use placeholders you need to install the following plugins on your server.
* Placeholder API (Required) - https://www.spigotmc.org/resources/placeholderapi.6245/
* Protocol Lib (Required) - https://www.spigotmc.org/resources/protocollib.1997/
* Holographic Extension (Holographic Displays) - https://www.spigotmc.org/resources/holographicextension.18461/

### Available Placeholders

| Placeholder                     | Description                                                                |
|---------------------------------|----------------------------------------------------------------------------|
| %ep_top_donator_1_username%     | The username of the player that has donated the most.                      |
| %ep_top_donator_1_total%        | The total amount donated by the first player.                              |
| %ep_top_donator_2_username%     | The username of the player that has donated the second most.               |
| %ep_top_donator_2_total%        | The total amount donated by the second player.                             |
| %ep_top_donator_3_username%     | The username of the player that has donated the third most.                |
| %ep_top_donator_3_total%        | The total amount donated by the third player.                              |
| %ep_top_donator_day_username%   | The username of the player that has donated the most in the last 24 hours. |
| %ep_top_donator_day_total%      | The total amount donated by the top day donator.                           |
| %ep_top_donator_week_username%  | The username of the player that has donated the most in the last week.     |
| %ep_top_donator_week_total%     | The total amount donated by the top week donator.                          |
| %ep_top_donator_month_username% | The username of the player that has donated the most in the last month.    |
| %ep_top_donator_month_total%    | The total amount donated by the top month donator.                         |
| %ep_latest_donator_username%    | The username of the player that has donated most recently.                 |
