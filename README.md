# Pentamana

Pentamana is a scoreboard-based mana mod that runs server-side providing mana modification and damage calculation hooks. It introduces mana as a base stat into Minecraft. A row of aqua stars utilizing the actionbar respresents the player's current mana supply and capacity.

## Mana Mechanic

Each player starts with 33,554,431 mana capacity (1 star in the manabar). Mana capacity is maxed out at 2,147,483,647 mana, or 64 stars total. The mana capacity would be calculated by the formula below:

``` txt
Mana Capacity = 33554431 + Capacity Enchantment Level * 33554432
```

A player basicly regenerate 1,048,576 mana every tick (32 ticks per star). The mana regeneration amount per tick would be calculated by the formula below:

``` txt
Mana Regen = 1048576 + Emanation Enchantment Level * 65536
```

The output magic damage from casting would be calculated by the formula below: (Can be got via `ServerPlayerEntity.getMagicDamageAgainst(Entity entity, float amount)`)

``` txt
Magic Damage = amount * (Mana Capacity / Mana Scale) + [0.5 + Potency Enchantment Level * 0.5](if potency presented)
```

## Enchantments

Enchantment damage would need additional apply.

### Capacity

- Maximum level: II
- Primary items: Stick
- Secondary items: Axe, Hoe, Mace, Pickaxe, Shovel, Sword, Trident
- Enchantment weight: 2

Capacity adds extra mana capacity 33,554,432 per level.

### Emanation

- Maximum level: II
- Primary items: Stick
- Secondary items: Axe, Hoe, Mace, Pickaxe, Shovel, Sword, Trident
- Enchantment weight: 5

Emanation adds extra mana regeneration 65,536 per level.

### Potency

- Maximum level: V
- Primary items: Stick
- Secondary items: Axe, Hoe, Mace, Pickaxe, Shovel, Sword, Trident
- Enchantment weight: 10

Potency adds 1 extra casting magic damage for the first level and 0.5 for all subsequent levels.

### Utilization

- Maximum level: V
- Primary items: Stick
- Secondary items: Axe, Hoe, Mace, Pickaxe, Shovel, Sword, Trident
- Enchantment weight: 5

Utilization reduces the mana cost of casting by 10% per level.

## Events

- `TickManaCallback` Called after the mana capacity calculation, before everything else.

- `RegenManaCallback` Called when a player is regenerating mana. After the mana regeneration calculation, before regenerating mana.

- `ConsumeManaCallback` Called when a player is consuming mana. After the mana consumption calculation, before consuming mana.
