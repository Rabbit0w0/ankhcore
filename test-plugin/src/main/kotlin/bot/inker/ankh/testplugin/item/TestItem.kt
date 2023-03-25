package bot.inker.ankh.testplugin.item

import bot.inker.ankh.core.api.plugin.AnkhPluginContainer
import bot.inker.ankh.core.api.plugin.annotations.AutoRegistered
import bot.inker.ankh.core.api.world.WorldService
import bot.inker.ankh.core.common.dsl.key
import bot.inker.ankh.core.common.dsl.logger
import bot.inker.ankh.core.item.AbstractAnkhItem
import bot.inker.ankh.testplugin.block.TestBlock
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerInteractEvent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@AutoRegistered
class TestItem @Inject private constructor(
  private val pluginContainer: AnkhPluginContainer,
  private val worldService: WorldService,
  private val testBlockFactory: TestBlock.Factory,
) : AbstractAnkhItem() {
  private val logger by logger()

  override fun key(): Key = pluginContainer.key("test-item")
  override fun material(): Material = Material.STONE

  override fun itemName(): Component = Component.text()
    .append(Component.text("测", NamedTextColor.BLUE))
    .append(Component.text("试", NamedTextColor.RED))
    .append(Component.text("物", NamedTextColor.GREEN))
    .append(Component.text("品", NamedTextColor.YELLOW))
    .build()

  override fun lores(): List<Component> = listOf(
    Component.text()
      .append(Component.text("测试物品，点击放置 测试方块"))
      .build()
  )

  override fun onBlockPlace(event: BlockPlaceEvent) {
    worldService.setBlock(event.blockPlaced.location, testBlockFactory.create())
  }

  override fun onUseItem(event: PlayerInteractEvent) {
    event.player.sendMessage("You use test block as action ${event.action.name}")
    when (event.action) {
      Action.RIGHT_CLICK_BLOCK -> {
        return // TODO: use item
        val clickedBlock = event.clickedBlock ?: return
        worldService.setBlock(
          clickedBlock.location.add(event.blockFace.direction),
          testBlockFactory.create()
        )
      }

      else -> {}
    }
  }
}