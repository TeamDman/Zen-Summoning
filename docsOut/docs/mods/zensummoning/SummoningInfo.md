# SummoningInfo

This class was added by a mod with mod-id `zensummoning`. So you need to have this mod installed if you want to use this feature.

## Importing the class

It might be required for you to import the package if you encounter any issues (like casting an Array), so better be safe than sorry and add the import at the very top of the file.
```zenscript
import mods.zensummoning.SummoningInfo;
```


## Static Methods

:::group{name=create}

Return Type: [SummoningInfo](/mods/zensummoning/SummoningInfo)

```zenscript
// SummoningInfo.create() as SummoningInfo

SummoningInfo.create();
```

:::

## Methods

:::group{name=addMob}

Return Type: [SummoningInfo](/mods/zensummoning/SummoningInfo)

```zenscript
SummoningInfo.addMob(info as MobInfo) as SummoningInfo
```

| Parameter | Type | Description |
|-----------|------|-------------|
| info | [MobInfo](/mods/zensummoning/MobInfo) | No Description Provided |


:::

:::group{name=setCatalyst}

Return Type: [SummoningInfo](/mods/zensummoning/SummoningInfo)

```zenscript
SummoningInfo.setCatalyst(ingredient as IIngredientWithAmount) as SummoningInfo
```

| Parameter | Type | Description |
|-----------|------|-------------|
| ingredient | [IIngredientWithAmount](/vanilla/api/items/IIngredientWithAmount) | No Description Provided |


:::

:::group{name=setConsumeCatalyst}

Return Type: [SummoningInfo](/mods/zensummoning/SummoningInfo)

```zenscript
SummoningInfo.setConsumeCatalyst(value as boolean) as SummoningInfo
```

| Parameter | Type | Description |
|-----------|------|-------------|
| value | boolean | No Description Provided |


:::

:::group{name=setMutator}

Return Type: [SummoningInfo](/mods/zensummoning/SummoningInfo)

```zenscript
SummoningInfo.setMutator(mutator as Consumer<SummoningAttempt>) as SummoningInfo
```

| Parameter | Type | Description |
|-----------|------|-------------|
| mutator | Consumer&lt;[SummoningAttempt](/mods/zensummoning/SummoningAttempt)&gt; | No Description Provided |


:::

:::group{name=setReagents}

Return Type: [SummoningInfo](/mods/zensummoning/SummoningInfo)

```zenscript
SummoningInfo.setReagents(reagents as IIngredientWithAmount[]) as SummoningInfo
```

| Parameter | Type | Description |
|-----------|------|-------------|
| reagents | [IIngredientWithAmount](/vanilla/api/items/IIngredientWithAmount)[] | No Description Provided |


:::

:::group{name=setWeight}

Return Type: [SummoningInfo](/mods/zensummoning/SummoningInfo)

```zenscript
SummoningInfo.setWeight(weight as double) as SummoningInfo
```

| Parameter | Type | Description |
|-----------|------|-------------|
| weight | double | No Description Provided |


:::


