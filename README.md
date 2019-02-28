# ZenSummoning
A mod that allows for pack creators to set up custom summoning situations using ZenScript

## Example Script
```JavaScript
import crafttweaker.item.IItemStack;
import crafttweaker.item.IIngredient;
import mods.zensummoning.SummoningDirector;
import mods.zensummoning.SummoningAttempt;

SummoningDirector.enableDebugging();
SummoningDirector.addSummonInfo(
    <minecraft:stick>,
    [<minecraft:stone>],
    "minecraft",
    "cow",
    2
);

SummoningDirector.addSummonInfo(
    <minecraft:stone>*2,
    [<minecraft:dirt>*5,<minecraft:egg>*64],
    "minecraft",
    "chicken",
    10,
    {"Health":200, "Attributes":[{"Name":"generic.maxHealth", "Base":200}]}
);

SummoningDirector.addSummonInfo(
    <minecraft:planks>*5,
    [<minecraft:glass>, <minecraft:blaze_rod>*256],
    "minecraft",
    "blaze",
    4,
    {},
    function(attempt as SummoningAttempt) {
        if (attempt.world.raining) {
            attempt.success = false;
            attempt.message = "Can not summon blazes in the rain!";
        }
    }
);
```
Values, in order:
```
ItemStack catalyst
ItemStack[] list_reagents
String mob_modid
String mob_name

//You may omit these
int height_above_altar
NBTTagCompound NBTData ({} for none)
Consumer<SummoningAttemot>
```

The `SummoningAttempt` object has to properties, `boolean success` and `String message`.

Setting `success=false` will cancel an otherwise valid summoning attempt.

Setting the `message` property will set the string to be displayed to the user when their attempt either succeeds or fails.

[Example video](https://streamable.com/hflui)

[Another](https://streamable.com/snlbk)