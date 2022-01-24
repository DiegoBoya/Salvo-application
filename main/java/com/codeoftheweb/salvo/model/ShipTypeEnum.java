package com.codeoftheweb.salvo.model;

public enum ShipTypeEnum {
    DESTROYER(5), PATROL_BOAT(2), BARQUITO_FIESTERO(2), CARRIER(5), BATTLESHIP(4), SUBMARINE(3);

    private int length;

    ShipTypeEnum(int length) {
        this.length = length;
    }

    public int getLength(){
        return length;
    }
}

