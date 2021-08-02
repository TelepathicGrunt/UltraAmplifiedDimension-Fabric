package com.telepathicgrunt.ultraamplifieddimension.configs;


import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "ultra_amplified_dimension")
public class UADConfig implements ConfigData {

    @ConfigEntry.Gui.PrefixText
    @ConfigEntry.Gui.Tooltip(count = 0)
    @Comment(value = """

            Adds very heavy fog to make the world look more spooky and limit visibility.
            This is not the same as distance fog which only applies weakly to chunks in the far distance.""")
    public boolean heavyFog = false;

    @ConfigEntry.Gui.PrefixText
    @ConfigEntry.Gui.Tooltip(count = 0)
    @ConfigEntry.BoundedDiscrete(min = -500, max = 500)
    @Comment(value = """
            
            Maximum height for clouds to be at. Default is 245.""")
    public int cloudHeight = 245;

    @ConfigEntry.Gui.PrefixText
    @ConfigEntry.Gui.Tooltip(count = 0)
    @Comment(value = """

            Lets Nether Portals be able to be created in Ultra Amplified Dimension.
            Using the portal in this dimension will take you to the Nether but Nether
            Portals in the Nether will take you to the Overworld instead. So this option
            is good if you want a second way of escaping the Ultra Amplified Dimension.""")
    public boolean allowNetherPortal = false;

    @ConfigEntry.Gui.PrefixText
    @ConfigEntry.Gui.Tooltip(count = 0)
    @Comment(value = """

            Makes leaving the Ultra Amplified Dimension by Amplified Portal Block always
            places you back in the Overworld regardless of which dimension you originally came
            from. Use this option if this dimension becomes locked in with another dimension
            so you are stuck teleporting between the two and cannot get back to the Overworld""")
    public boolean forceExitToOverworld = false;
}
