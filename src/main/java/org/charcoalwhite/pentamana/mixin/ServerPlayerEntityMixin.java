package org.charcoalwhite.pentamana.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.scoreboard.ScoreAccess;
import net.minecraft.scoreboard.ScoreHolder;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import static org.charcoalwhite.pentamana.Pentamana.CAPACITY;
import static org.charcoalwhite.pentamana.Pentamana.EMANATION;
import static org.charcoalwhite.pentamana.Pentamana.MANA_CAPACITY_BASE;
import static org.charcoalwhite.pentamana.Pentamana.MANA_CAPACITY_EXPANSION_BASE;
import static org.charcoalwhite.pentamana.Pentamana.MANA_REGEN_BASE;
import static org.charcoalwhite.pentamana.Pentamana.MANA_REGEN_EXPANSION_BASE;
import static org.charcoalwhite.pentamana.Pentamana.MANA_SCALE;
import static org.charcoalwhite.pentamana.Pentamana.MANA_STAR_BROKEN;
import static org.charcoalwhite.pentamana.Pentamana.MANA_STAR_HOLLOW;
import static org.charcoalwhite.pentamana.Pentamana.MANA_STAR_INTACT;
import static org.charcoalwhite.pentamana.Pentamana.MAX_MANABAR_LIFE;
import static org.charcoalwhite.pentamana.Pentamana.POTENCY;
import static org.charcoalwhite.pentamana.Pentamana.UTILIZATION;
import static org.charcoalwhite.pentamana.Pentamana.scoreboard;
import static org.charcoalwhite.pentamana.Pentamana.manaCapacityObjective;
import static org.charcoalwhite.pentamana.Pentamana.manaObjective;
import static org.charcoalwhite.pentamana.Pentamana.manaRegenObjective;
import static org.charcoalwhite.pentamana.Pentamana.manaConsumObjective;
import static org.charcoalwhite.pentamana.Pentamana.manabarLifeObjective;
import org.charcoalwhite.pentamana.api.ConsumeManaCallback;
import org.charcoalwhite.pentamana.api.RegenManaCallback;
import org.charcoalwhite.pentamana.api.ServerPlayerEntityApi;
import org.charcoalwhite.pentamana.api.TickManaCallback;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin implements ServerPlayerEntityApi {
	@Inject(method = "tick()V", at = @At("RETURN"))
	private void tickMana(CallbackInfo info) {
		this.tickMana();
	}

	@Override
	public void tickMana() {
		this.incrementManabarLife();

		this.setManaCapacity(MANA_CAPACITY_BASE);
		int level = ((ServerPlayerEntity)(Object)this)
			.getWeaponStack()
			.getEnchantments()
			.getLevel(CAPACITY);
		if (level != 0) {
			this.incrementManaCapacity(MANA_CAPACITY_EXPANSION_BASE * level);
		}

		TickManaCallback.EVENT.invoker().interact((ServerPlayerEntity)(Object)this);

		int mana = this.getMana();
		int manaCapacity = this.getManaCapacity();
		int manabarLife = this.getManabarLife();
		if (mana < manaCapacity && mana >= 0) {
			this.regenMana();
		} else if (mana != manaCapacity) {
			mana = manaCapacity;
			this.setMana(mana);

			if (manabarLife <= 0) {
				this.updateMana();
			}

		} else if (manabarLife == 0 || manabarLife == MAX_MANABAR_LIFE) {
			this.updateMana();
		}
    };

	@Override
    public boolean regenMana() {
		boolean isFullRegen = true;

		this.setManaRegen(MANA_REGEN_BASE);
		int level = ((ServerPlayerEntity)(Object)this)
			.getWeaponStack()
			.getEnchantments()
			.getLevel(EMANATION);
		if (level != 0) {
			this.incrementManaRegen(MANA_REGEN_EXPANSION_BASE * level);
		}

		RegenManaCallback.EVENT.invoker().interact((ServerPlayerEntity)(Object)this);

		int mana = this.getMana();
		int manaCapacity = this.getManaCapacity();
		int manaRegen = this.getManaRegen();
		int manabarLife = this.getManabarLife();
		mana += manaRegen;
		if (mana > manaCapacity || mana < 0) {
			mana = manaCapacity;
			isFullRegen = false;
		}

		this.setMana(mana);
		if (manabarLife <= 0) {
			this.updateMana();
		}

		return isFullRegen;
    }
	
	@Override
	public boolean consumeMana() {
		int level = ((ServerPlayerEntity)(Object)this)
			.getWeaponStack()
			.getEnchantments()
			.getLevel(UTILIZATION);
		if (level != 0) {
			this.setManaConsume(this.getManaConsume() * (10 - level) / 10);
		}

		ConsumeManaCallback.EVENT.invoker().interact((ServerPlayerEntity)(Object)this);

		int mana = this.getMana();
		int manaConsume = this.getManaConsume();
		int manabarLife = this.getManabarLife();
		mana -= manaConsume;
		if (mana >= 0) {
			this.setMana(mana);
			if (manabarLife <= 0) {
				this.updateMana();
			}

			return true;
		}

		return false;
	}

	@Override
    public void updateMana() {
		this.setManabarLife(-MAX_MANABAR_LIFE);
		
		int mana = this.getMana();
		int manaCapacity = this.getManaCapacity();
		mana = (-mana - 1) / -MANA_SCALE;
		manaCapacity = (-manaCapacity - 1) / -MANA_SCALE;

		StringBuilder manabar = new StringBuilder();
		for (int i = mana / 2; i > 0; --i) {
			manabar.append(MANA_STAR_INTACT);
		}

		if (mana % 2 == 1 && mana != manaCapacity) {
			manabar.append(MANA_STAR_BROKEN);
		}

		for (int i = (manaCapacity - mana - mana % 2) / 2; i > 0; --i) {
			manabar.append(MANA_STAR_HOLLOW);
		}

		this.sendMessage(Text.literal(manabar.toString()).formatted(Formatting.AQUA), true);
    }

	@Override
	public float getMagicDamageAgainst(Entity entity, float amount) {
		int manaCapacity = this.getManaCapacity();
		float potencyDamage = 0;
		int level = ((ServerPlayerEntity)(Object)this)
			.getWeaponStack()
			.getEnchantments()
			.getLevel(POTENCY);
		if (level != 0) {
			potencyDamage = (float) (0.5 + level * 0.5);
		}

		return amount * manaCapacity / MANA_SCALE + potencyDamage;
	}

	@Override
	public int getMana() {
		ScoreHolder scoreHolder = this.getScoreHolder();
        ScoreAccess manaScore = scoreboard.getOrCreateScore(scoreHolder, manaObjective);
		return manaScore.getScore();
	}

	@Override
    public void setMana(int mana) {
		ScoreHolder scoreHolder = this.getScoreHolder();
        ScoreAccess manaScore = scoreboard.getOrCreateScore(scoreHolder, manaObjective);
		manaScore.setScore(mana);
    }

	@Override
	public int incrementMana(int mana) {
		ScoreHolder scoreHolder = this.getScoreHolder();
        ScoreAccess manaScore = scoreboard.getOrCreateScore(scoreHolder, manaObjective);
		return manaScore.incrementScore(mana);
	}

	@Override
	public int incrementMana() {
		ScoreHolder scoreHolder = this.getScoreHolder();
        ScoreAccess manaScore = scoreboard.getOrCreateScore(scoreHolder, manaObjective);
		return manaScore.incrementScore();
	}

	@Override
    public void resetMana() {
		ScoreHolder scoreHolder = this.getScoreHolder();
        ScoreAccess manaScore = scoreboard.getOrCreateScore(scoreHolder, manaObjective);
		manaScore.resetScore();
    }

	@Override
	public int getManaCapacity() {
		ScoreHolder scoreHolder = this.getScoreHolder();
        ScoreAccess manaCapacityScore = scoreboard.getOrCreateScore(scoreHolder, manaCapacityObjective);
		return manaCapacityScore.getScore();
	}
	
	@Override
    public void setManaCapacity(int manaCapacity) {
		ScoreHolder scoreHolder = this.getScoreHolder();
        ScoreAccess manaCapacityScore = scoreboard.getOrCreateScore(scoreHolder, manaCapacityObjective);
		manaCapacityScore.setScore(manaCapacity);
    }

	@Override
	public int incrementManaCapacity(int manaCapacity) {
		ScoreHolder scoreHolder = this.getScoreHolder();
        ScoreAccess manaCapacityScore = scoreboard.getOrCreateScore(scoreHolder, manaCapacityObjective);
		return manaCapacityScore.incrementScore(manaCapacity);
	}

	@Override
	public int incrementManaCapacity() {
		ScoreHolder scoreHolder = this.getScoreHolder();
        ScoreAccess manaCapacityScore = scoreboard.getOrCreateScore(scoreHolder, manaCapacityObjective);
		return manaCapacityScore.incrementScore();
	}

	@Override
    public void resetManaCapacity() {
		ScoreHolder scoreHolder = this.getScoreHolder();
        ScoreAccess manaCapacityScore = scoreboard.getOrCreateScore(scoreHolder, manaCapacityObjective);
		manaCapacityScore.resetScore();
    }

	@Override
	public int getManaRegen() {
		ScoreHolder scoreHolder = this.getScoreHolder();
        ScoreAccess manaRegenScore = scoreboard.getOrCreateScore(scoreHolder, manaRegenObjective);
		return manaRegenScore.getScore();
	}

	@Override
    public void setManaRegen(int manaRegen) {
		ScoreHolder scoreHolder = this.getScoreHolder();
        ScoreAccess manaRegenScore = scoreboard.getOrCreateScore(scoreHolder, manaRegenObjective);
		manaRegenScore.setScore(manaRegen);
    }

	@Override
	public int incrementManaRegen(int manaRegen) {
		ScoreHolder scoreHolder = this.getScoreHolder();
        ScoreAccess manaRegenScore = scoreboard.getOrCreateScore(scoreHolder, manaRegenObjective);
		return manaRegenScore.incrementScore(manaRegen);
	}

	@Override
	public int incrementManaRegen() {
		ScoreHolder scoreHolder = this.getScoreHolder();
        ScoreAccess manaRegenScore = scoreboard.getOrCreateScore(scoreHolder, manaRegenObjective);
		return manaRegenScore.incrementScore();
	}

	@Override
    public void resetManaRegen() {
		ScoreHolder scoreHolder = this.getScoreHolder();
        ScoreAccess manaRegenScore = scoreboard.getOrCreateScore(scoreHolder, manaRegenObjective);
		manaRegenScore.resetScore();
    }

	@Override
	public int getManaConsume() {
		ScoreHolder scoreHolder = this.getScoreHolder();
        ScoreAccess manaConsumeScore = scoreboard.getOrCreateScore(scoreHolder, manaConsumObjective);
		return manaConsumeScore.getScore();
	}

	@Override
    public void setManaConsume(int manaConsume) {
		ScoreHolder scoreHolder = this.getScoreHolder();
        ScoreAccess manaConsumeScore = scoreboard.getOrCreateScore(scoreHolder, manaConsumObjective);
		manaConsumeScore.setScore(manaConsume);
    }

	@Override
	public int incrementManaConsume(int manaConsume) {
		ScoreHolder scoreHolder = this.getScoreHolder();
        ScoreAccess manaConsumeScore = scoreboard.getOrCreateScore(scoreHolder, manaConsumObjective);
		return manaConsumeScore.incrementScore(manaConsume);
	}

	@Override
	public int incrementManaConsume() {
		ScoreHolder scoreHolder = this.getScoreHolder();
        ScoreAccess manaConsumeScore = scoreboard.getOrCreateScore(scoreHolder, manaConsumObjective);
		return manaConsumeScore.incrementScore();
	}

	@Override
    public void resetManaConsume() {
		ScoreHolder scoreHolder = this.getScoreHolder();
        ScoreAccess manaConsumeScore = scoreboard.getOrCreateScore(scoreHolder, manaConsumObjective);
		manaConsumeScore.resetScore();
    }

	@Override
	public int getManabarLife() {
		ScoreHolder scoreHolder = this.getScoreHolder();
        ScoreAccess manabarLifeScore = scoreboard.getOrCreateScore(scoreHolder, manabarLifeObjective);
		return manabarLifeScore.getScore();
	}

	@Override
    public void setManabarLife(int manabarLife) {
		ScoreHolder scoreHolder = this.getScoreHolder();
        ScoreAccess manabarLifeScore = scoreboard.getOrCreateScore(scoreHolder, manabarLifeObjective);
		manabarLifeScore.setScore(manabarLife);
    }
	
	@Override
	public int incrementManabarLife(int manabarLife) {
		ScoreHolder scoreHolder = this.getScoreHolder();
        ScoreAccess manabarLifeScore = scoreboard.getOrCreateScore(scoreHolder, manabarLifeObjective);
		return manabarLifeScore.incrementScore(manabarLife);
	}

	@Override
	public int incrementManabarLife() {
		ScoreHolder scoreHolder = this.getScoreHolder();
        ScoreAccess manabarLifeScore = scoreboard.getOrCreateScore(scoreHolder, manabarLifeObjective);
		return manabarLifeScore.incrementScore();
	}

	@Override
    public void resetManabarLife() {
		ScoreHolder scoreHolder = this.getScoreHolder();
        ScoreAccess manabarLifeScore = scoreboard.getOrCreateScore(scoreHolder, manabarLifeObjective);
		manabarLifeScore.resetScore();
    }

	@Shadow
	public abstract void sendMessage(Text message, boolean overlay);

	@Shadow
	public abstract ScoreHolder getScoreHolder();
}