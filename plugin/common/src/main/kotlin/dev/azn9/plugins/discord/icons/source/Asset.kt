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

package dev.azn9.plugins.discord.icons.source

import java.awt.image.BufferedImage

const val ASSET_URL = "https://raw.githubusercontent.com/Azn9/JetBrains-Discord-Integration/develop/icons/data"

interface Asset {
    val id: String
    val theme: Theme

    fun getImage(size: Int?): BufferedImage?
    fun getUrl(): String
}