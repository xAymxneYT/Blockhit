@file:OptIn(org.polyfrost.oneconfig.api.ui.v1.ExperimentalOneConfigUiApi::class, androidx.compose.foundation.layout.ExperimentalLayoutApi::class)

package dev.blockhit.ui

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.blockhit.BlockhitClient
import dev.blockhit.config.BlockhitConfig
import dev.blockhit.sound.*
import org.polyfrost.oneconfig.api.config.v1.Visualizer
import org.polyfrost.oneconfig.api.ui.v1.OneConfigUI
import org.polyfrost.oneconfig.api.ui.v1.components.Chip
import org.polyfrost.oneconfig.api.ui.v1.components.Text
import org.polyfrost.oneconfig.api.ui.v1.page.*
import org.polyfrost.oneconfig.api.ui.v1.theme.OneConfigTheme
import org.polyfrost.oneconfig.internal.ui.components.item.ItemIcon
import org.polyfrost.oneconfig.internal.ui.components.picker.PickerEntry
import org.polyfrost.oneconfig.internal.ui.components.picker.PickerSearchField
import org.polyfrost.oneconfig.internal.ui.components.picker.PickerTile
import org.polyfrost.oneconfig.internal.ui.components.settings.LocalOptionWidth

class SoundBrowserPage private constructor(
    private val config: BlockhitConfig,
    private val layer: Boolean,
) : OneConfigPage(
    if (layer) "blockhit:second-sound" else "blockhit:sounds",
    PageText.literal(if (layer) "Choose second sound" else "Choose sound"),
    "qol",
    PageGroup.MODS_AND_OPTIONS,
) {
    override val showInSidebar = false

    @Composable
    override fun Content() {
        val theme = OneConfigTheme.current
        val available = remember { SoundCatalog.entries().filter(BlockhitClient.get()::available) }
        var selected by remember { mutableIntStateOf(if (layer) config.layerSound else config.sound) }
        var group by remember { mutableStateOf(SoundGroup.RECOMMENDED) }
        var query by remember { mutableStateOf("") }
        val entries = remember(available, group, query) {
            SoundBrowserModel.filter(available, group, query) { true }
        }
        val scroll = rememberLazyListState()
        LaunchedEffect(group, query) { scroll.scrollToItem(0) }
        DisposableEffect(Unit) { onDispose { BlockhitClient.get().stopPreview() } }

        Column(
            Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(SoundCatalog.name(selected), Modifier, theme.textColor, 16.sp)
                    Text("Select a sound to keep it. Preview uses the controls below.", Modifier, theme.textColorSecondary, 12.sp)
                }
                Chip("Done", false, "tick") {
                    config.save()
                    OneConfigUI.open(OneConfigPages.modConfig(config.id, if (layer) "Advanced" else "General"))
                }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Volume", Modifier, theme.textColor, 13.sp)
                    NativeSlider(if (layer) "layerVolume" else "volume")
                }
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Pitch", Modifier, theme.textColor, 13.sp)
                    NativeSlider(if (layer) "layerPitch" else "pitch")
                }
            }
            PickerSearchField(query, { query = it }, autoFocus = false, placeholderKey = "Search sounds")
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SoundGroup.values().forEach { category ->
                    Chip(category.title, group == category) { group = category }
                }
            }
            Box(Modifier.weight(1f).fillMaxWidth()) {
                LazyColumn(
                    state = scroll,
                    modifier = Modifier.fillMaxSize().padding(end = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 8.dp),
                ) {
                    items(entries, key = { it.id() }) { entry ->
                        SoundRow(entry, selected == entry.index()) {
                            config.selectSound(entry.index(), layer)
                            selected = entry.index()
                            BlockhitClient.get().previewSound(entry, layer)
                        }
                    }
                    if (entries.isEmpty()) item {
                        Text("No matching sounds.", Modifier.padding(16.dp), theme.textColorSecondary, 14.sp)
                    }
                }
                VerticalScrollbar(rememberScrollbarAdapter(scroll), Modifier.align(Alignment.CenterEnd).fillMaxHeight())
            }
        }
    }

    @Composable
    private fun NativeSlider(name: String) {
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            CompositionLocalProvider(LocalOptionWidth provides (maxWidth - 110.dp).coerceAtLeast(60.dp)) {
                remember { Visualizer.SliderVisualizer() }.visualize(config.control(name))
            }
        }
    }

    @Composable
    private fun SoundRow(entry: SoundEntry, selected: Boolean, select: () -> Unit) {
        val theme = OneConfigTheme.current
        val accent = OneConfigTheme.accent
        val tile = remember(entry) { SoundTile(entry) }
        Row(
            modifier = Modifier.fillMaxWidth().clip(theme.modCardShape)
                .background(if (selected) accent.copy(alpha = .12f) else theme.componentBackground)
                .border(1.dp, if (selected) accent else theme.borderColor, theme.modCardShape)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            PickerTile(
                entry = tile,
                icon = { item, modifier, alpha -> ItemIcon(item.icon, modifier, framed = false, alpha = alpha) },
                modifier = Modifier.size(44.dp),
                selected = selected,
                onClick = select,
            )
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(entry.name(), Modifier, theme.textColor, 14.sp)
                Text(if (entry.recommended()) "Recommended" else entry.group().title, Modifier, theme.textColorSecondary, 11.sp)
            }
            Chip("Preview", false) { BlockhitClient.get().previewSound(entry, layer) }
            Chip(if (selected) "Selected" else "Select", selected) { select() }
        }
    }

    private data class SoundTile(val sound: SoundEntry) : PickerEntry {
        override val id get() = "minecraft:" + sound.id()
        override val displayName get() = sound.name()
        val icon get() = when {
            sound.id().contains("anvil") -> "minecraft:anvil"
            sound.id().contains("bow") || sound.id().contains("successful_hit") -> "minecraft:bow"
            sound.id().contains("orb") || sound.id().contains("levelup") -> "minecraft:experience_bottle"
            sound.id().contains("chest") -> "minecraft:chest"
            sound.id().contains("piston") -> "minecraft:piston"
            else -> when (sound.group()) {
                SoundGroup.BLOCKS -> "minecraft:stone"
                SoundGroup.MOBS -> "minecraft:spawn_egg"
                SoundGroup.NOTES -> "minecraft:noteblock"
                SoundGroup.INTERFACE -> "minecraft:stone_button"
                SoundGroup.MUSIC -> "minecraft:record_cat"
                SoundGroup.COMBAT -> "minecraft:iron_sword"
                else -> "minecraft:ender_pearl"
            }
        }
    }

    companion object {
        private var main: SoundBrowserPage? = null
        private var second: SoundBrowserPage? = null

        @JvmStatic
        fun open(config: BlockhitConfig, layer: Boolean) {
            val page = if (layer) second else main
            val target = page ?: SoundBrowserPage(config, layer).also {
                OneConfigPages.register(it)
                if (layer) second = it else main = it
            }
            OneConfigUI.open(target)
        }
    }
}
