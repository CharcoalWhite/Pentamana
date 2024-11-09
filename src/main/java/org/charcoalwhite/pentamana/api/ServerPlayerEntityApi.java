package org.charcoalwhite.pentamana.api;

import net.minecraft.entity.Entity;

public interface ServerPlayerEntityApi {
    default void tickMana() {

    };

    default boolean regenMana() {
        return false;
    }

    default boolean consumeMana() {
        return false;
    }

    default void updateMana() {

    }

    default float getMagicDamageAgainst(Entity entity, float amount) {
        return 0;
    }

    default int getMana() {
        return 0;
    }

    default void setMana(int playerMana) {
        
    }

    default int incrementMana(int playerMana) {
        return 0;
    }

    default int incrementMana() {
        return 0;
    }

    default void resetMana() {

    }

    default int getManaCapacity() {
        return 0;
    }

    default void setManaCapacity(int playerManaCapacity) {

    }

    default int incrementManaCapacity(int playerMana) {
        return 0;
    }

    default int incrementManaCapacity() {
        return 0;
    }

    default void resetManaCapacity() {
        
    }

    default int getManaRegen() {
        return 0;
    }

    default void setManaRegen(int playerMana) {
        
    }

    default int incrementManaRegen(int playerMana) {
        return 0;
    }

    default int incrementManaRegen() {
        return 0;
    }

    default void resetManaRegen() {

    }

    default int getManaConsume() {
        return 0;
    }

    default void setManaConsume(int playerMana) {
        
    }

    default int incrementManaConsume(int playerMana) {
        return 0;
    }

    default int incrementManaConsume() {
        return 0;
    }

    default void resetManaConsume() {

    }

    default int getManabarLife() {
        return 0;
    }

    default void setManabarLife(int playerManabarLife) {

    }

    default int incrementManabarLife(int playerMana) {
        return 0;
    }

    default int incrementManabarLife() {
        return 0;
    }

    default void resetManabarLife() {
        
    }
}