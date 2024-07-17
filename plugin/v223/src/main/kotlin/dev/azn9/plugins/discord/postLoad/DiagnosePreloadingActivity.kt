/*
 * Copyright 2017-2020 Aljoscha Grebe
 * Copyright 2023-2024 Axel JOLY (Azn9) <contact@azn9.dev>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.azn9.plugins.discord.postLoad

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.application.ApplicationNamesInfo
import dev.azn9.plugins.discord.DiscordPlugin
import dev.azn9.plugins.discord.diagnose.diagnoseService
import dev.azn9.plugins.discord.settings.values.ApplicationType
import dev.azn9.plugins.discord.utils.DisposableCoroutineScope
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import dev.azn9.plugins.discord.diagnose.DiagnoseService
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.future.asCompletableFuture
import kotlinx.coroutines.launch

class DiagnosePreloadingActivity : StartupActivity.Background, StartupActivity.DumbAware, DisposableCoroutineScope {

    override val parentJob: Job = SupervisorJob()

    override fun runActivity(project: Project) {
        launch {
            diagnose()
        }
    }

    private fun diagnose() {
        DiscordPlugin.LOG.info("App starting, diagnosing environment")

        DiscordPlugin.LOG.info("Application identifiers: ${ApplicationType.IDE.applicationName}, ${ApplicationType.IDE_EDITION.applicationName}")

        if (DiscordPlugin.isAlmightyAlpacasPluginPresent()) {
            NotificationGroupManager.getInstance()
                .getNotificationGroup("dev.azn9.plugins.discord.notification.error")
                .createNotification(
                    "Discord Integration V2",
                    "Detected plugin 'Discord Integration' by Almighty Alpaca. Please uninstall this old plugin as it will prevent this one from working!",
                    NotificationType.ERROR
                )
                .setImportant(true)
                .run(Notifications.Bus::notify)
            return
        }

        diagnoseService.discord.asCompletableFuture().thenAcceptAsync { discord ->
            when (discord) {
                DiagnoseService.Discord.SNAP -> {
                    NotificationGroupManager.getInstance()
                        .getNotificationGroup("dev.azn9.plugins.discord.notification.error")
                        .createNotification("Discord Integration V2", "Discord detected as snap package, this will prevent the plugin from connecting to Discord!", NotificationType.ERROR)
                        .setImportant(true)
                        .run(Notifications.Bus::notify)
                }
                DiagnoseService.Discord.FLATPAK -> {
                    NotificationGroupManager.getInstance()
                        .getNotificationGroup("dev.azn9.plugins.discord.notification.error")
                        .createNotification("Discord Integration V2", "Discord detected a flatpak package, this may prevent the plugin from connecting to Discord!", NotificationType.WARNING)
                        .setImportant(true)
                        .run(Notifications.Bus::notify)
                }
                DiagnoseService.Discord.RUNNING_WITHOUT_RICH_PRESENCE_ENABLED -> {
                    NotificationGroupManager.getInstance()
                        .getNotificationGroup("dev.azn9.plugins.discord.notification.error")
                        .createNotification("Discord Integration V2", "Discord detected but rich presence is not enabled, this will prevent the plugin from connecting to Discord!", NotificationType.WARNING)
                        .setImportant(true)
                        .run(Notifications.Bus::notify)
                }
                DiagnoseService.Discord.BROWSER -> {
                    NotificationGroupManager.getInstance()
                        .getNotificationGroup("dev.azn9.plugins.discord.notification.error")
                        .createNotification("Discord Integration V2", "Discord detected in a web browser, this will prevent the plugin from connecting to Discord!", NotificationType.ERROR)
                        .setImportant(true)
                        .run(Notifications.Bus::notify)
                }
                DiagnoseService.Discord.ADMINISTRATOR -> {
                    NotificationGroupManager.getInstance()
                        .getNotificationGroup("dev.azn9.plugins.discord.notification.error")
                        .createNotification("Discord Integration V2", "Discord detected running as administrator, this will prevent the plugin from connecting to Discord!", NotificationType.ERROR)
                        .setImportant(true)
                        .run(Notifications.Bus::notify)
                }

                else -> {}
            }
        }
        diagnoseService.plugins.asCompletableFuture().thenAcceptAsync { plugins ->
            if (plugins == DiagnoseService.Plugins.ONE) {
                NotificationGroupManager.getInstance()
                    .getNotificationGroup("dev.azn9.plugins.discord.notification.error")
                    .createNotification(
                        "Discord Integration V2",
                        "An other Discord notification plugin has been detected, please uninstall it as it may prevent Discord Integration V2 from working correctly!",
                        NotificationType.WARNING
                    )
                    .setImportant(true)
                    .run(Notifications.Bus::notify)
            } else if (plugins == DiagnoseService.Plugins.MULTIPLE) {
                NotificationGroupManager.getInstance()
                    .getNotificationGroup("dev.azn9.plugins.discord.notification.error")
                    .createNotification(
                        "Discord Integration V2",
                        "Multiple other Discord notification plugins has been detected, please uninstall them as they may prevent Discord Integration V2 from working correctly!",
                        NotificationType.WARNING
                    )
                    .setImportant(true)
                    .run(Notifications.Bus::notify)
            }
        }
        diagnoseService.ide.asCompletableFuture().thenAcceptAsync { ide ->
            if (ide != DiagnoseService.Ide.OTHER) {
                NotificationGroupManager.getInstance()
                    .getNotificationGroup("dev.azn9.plugins.discord.notification.error")
                    .createNotification(
                        "Discord Integration V2",
                        "${ApplicationNamesInfo.getInstance().fullProductName} is running as a Snap package. This will most likely prevent the plugin from connection to your Discord client!",
                        NotificationType.WARNING
                    )
                    .setImportant(true)
                    .run(Notifications.Bus::notify)
            }
        }
    }
}
