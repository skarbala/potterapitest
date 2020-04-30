package models;

import com.fasterxml.jackson.annotation.JsonGetter;

public class Spell {
    private String id;
    private String spell;
    private String type;
    private String effect;
    private Boolean isUnforgivable;

    public Spell(){

    }

    public Spell(String spell, String type, String effect, Boolean isUnforgivable) {
        this.spell = spell;
        this.type = type;
        this.effect = effect;
        this.isUnforgivable = isUnforgivable;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSpell() {
        return spell;
    }

    public void setSpell(String spell) {
        this.spell = spell;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEffect() {
        return effect;
    }

    public void setEffect(String effect) {
        this.effect = effect;
    }

    @JsonGetter(value = "isUnforgivable")
    public Boolean getUnforgivable() {
        return isUnforgivable;
    }

    public void setUnforgivable(Boolean unforgivable) {
        isUnforgivable = unforgivable;
    }
}
// {
//    "id": "5b74ebd5fb6fc0739646754c",
//    "spell": "Aberto",
//    "type": "Charm",
//    "effect": "opens objects",
//    "isUnforgivable": false
//  }