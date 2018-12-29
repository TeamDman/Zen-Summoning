# ZenSummoning
A mod that allows for pack creators to set up custom summoning situations using ZenScript

## Example Script
```ZenScript
import crafttweaker.item.IItemStack;
import crafttweaker.item.IIngredient;
import mods.zensummoning.SummoningDirector;

SummoningDirector.addSummonInfo(<minecraft:stick>,[<minecraft:stone>],"minecraft","cow",5,{"Health":400});
SummoningDirector.addSummonInfo(<minecraft:stone>*2, [<minecraft:dirt>*5,<minecraft:egg>*64],"minecraft","chicken",10,{"Health":200, "Attributes":[{"Name":"generic.maxHealth", "Base":200}]});
```

[Example video](https://streamable.com/hflui)