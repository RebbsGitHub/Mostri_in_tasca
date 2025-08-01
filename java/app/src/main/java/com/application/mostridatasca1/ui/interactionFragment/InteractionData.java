package com.application.mostridatasca1.ui.interactionFragment;

public class InteractionData {
    public boolean died;
    public int life;
    public int experience;
    public int weapon;
    public int armor;
    public int amulet;

    public InteractionData(boolean died, int life, int experience, int weapon, int armor, int amulet) {
        this.died = died;
        this.life = life;
        this.experience = experience;
        this.weapon = weapon;
        this.armor = armor;
        this.amulet = amulet;
    }
}

/*
"died": false,
"life": 100,
"experience": 0,
"weapon": "181",
"armor": 22,
"amulet": null
*/