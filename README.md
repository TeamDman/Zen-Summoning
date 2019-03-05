# ZenSummoning
A mod that allows for pack creators to set up custom summoning situations using ZenScript

## Example Script
```JavaScript
import crafttweaker.item.IItemStack;
import crafttweaker.item.IIngredient;
import mods.zensummoning.SummoningDirector;
import mods.zensummoning.SummoningAttempt;
import mods.zensummoning.SummoningInfo;
import mods.zensummoning.MobInfo;

SummoningDirector.enableDebugging();
SummoningDirector.addSummonInfo(
    SummoningInfo.create()
        .setCatalyst(<minecraft:stick>)
        .setReagents([<minecraft:stone>, <minecraft:egg>*12])
        .addMob(MobInfo.create()
            .setMob("minecraft:cow")
            .setCount(4)
            .setOffset(0,4,0)
            .setSpread(1,1,1)
            .setData({"Health": 200, "Attributes":[{"Name":"generic.maxHealth","Base":200}]})
        )
        .addMob(MobInfo.create()
            .setMob("minecraft:blaze")
            .setCount(10)
            .setOffset(0,8,0)
            .setSpread(3,3,3)
            .setData({"Health": 2, "Attributes":[{"Name":"generic.maxHealth","Base":2}]})
        )
        .setMutator(function (attempt as SummoningAttempt) {
            if (attempt.world.raining) {
                attempt.success = false;
                attempt.message = "Can't summon this in the rain!";
            } else {
                attempt.message = "Woohoo!";
            }
        })
);

SummoningDirector.addSummonInfo(
    SummoningInfo.create()
        .setCatalyst(<minecraft:stone>*4)
        .setReagents([<minecraft:ladder>*512])
        .addMob(MobInfo.create()
            .setMob("minecraft:chicken")
        )
);
```
The second example gives a minimum-setup use case

The `SummoningAttempt` object has to properties, `boolean success` and `String message`.

Setting `success=false` will cancel an otherwise valid summoning attempt.

Setting the `message` property will set the string to be displayed to the user when their attempt either succeeds or fails.

![Example video](https://streamable.com/hflui)

![Another](https://streamable.com/snlbk)

![Another](https://streamable.com/7ay4r)

![What the JEI integration looks like](https://i.imgur.com/e6jLo6u.png)