package com.telepathicgrunt.ultraamplifieddimension.configs;

import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;
import me.sargunvohra.mcmods.autoconfig1u.shadowed.blue.endless.jankson.Comment;

@Config(name = "ultra_amplified_dimension")
public class UADConfig implements ConfigData {

    @ConfigEntry.Gui.PrefixText
    @ConfigEntry.Gui.Tooltip(count = 0)
    @Comment(value = "\n Adds very heavy fog to make the world look more spoky and limit visibility.\n"
            +" This is not the same as distance fog which does not make chunks near you foggy.")
    public boolean heavyFog = false;

    @ConfigEntry.Gui.PrefixText
    @ConfigEntry.Gui.Tooltip(count = 0)
    @ConfigEntry.BoundedDiscrete(min = 0, max = 2147483646)
    @Comment(value = "\n Maxium height for clouds to be at. Default is 245.")
    public int cloudHeight = 245;

    @ConfigEntry.Gui.PrefixText
    @ConfigEntry.Gui.Tooltip(count = 0)
    @Comment(value = "\n Lets Nether Portals be able to be created in Ultra Amplified DImension.\n"
            +" Using the portal in this dimension will take you to the Nether but Nether \n"
            +" Portals in the Nether will take you to the Overworld instead. So this option \n"
            +" is good if you want a second way of escaping the Ultra Amplified Dimension.")
    public boolean allowNetherPortal = false;

    @ConfigEntry.Gui.PrefixText
    @ConfigEntry.Gui.Tooltip(count = 0)
    @Comment(value = "\n Makes leaving the Ultra Amplified dimension always places you back\n"
            + " in the Overworld regardless of which dimension you originally \n"
            + " came from. Use this option if this dimension becomes locked in \n"
            + " with another dimension so you are stuck teleporting between the \n"
            + " two and cannot get back to the Overworld")
    public boolean forceExitToOverworld = false;

}
