/*
 *  Copyright (C) 2021 Lucas B. R. de Oliveira - IFSP/SCL
 *  Contact: lucas <dot> oliveira <at> ifsp <dot> edu <dot> br
 *
 *  This file is part of CTruco (Truco game for didactic purpose).
 *
 *  CTruco is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  CTruco is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with CTruco.  If not, see <https://www.gnu.org/licenses/>
 */

package com.bueno.application.model;

import com.bueno.domain.entities.deck.Card;
import com.bueno.domain.entities.deck.Suit;
import javafx.scene.image.Image;

import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

public class CardImage {

    private static final String IMAGES_PATH = "/images";
    private static final String IMAGES_EXTENTION = ".png";
    private Card card;
    private Image image;

    private static final HashMap<String, CardImage> cache = new HashMap<>();

    private CardImage(){
    }

    public static CardImage ofClosedCard(){
        return of(Card.getClosedCard());
    }

    public static CardImage ofNoCard(){
        return of(null);
    }

    public static CardImage of(Card card){
        final String imagePath = buildImagePath(getCardFileName(card));
        if(cache.containsKey(imagePath))
            return cache.get(imagePath);

        CardImage newInstance = new CardImage();
        newInstance.loadImage(imagePath);
        newInstance.card = card;

        cache.put(imagePath, newInstance);
        return newInstance;
    }

    private static String buildImagePath(String fileName) {
        return IMAGES_PATH + "/" + fileName + IMAGES_EXTENTION;
    }

    private static String getCardFileName(Card card) {
        if(card == null) return "table";
        if(card.equals(Card.getClosedCard())) return "red_back";

        final int rank = card.getRank();
        final Suit suit = card.getSuit();

        String rankName = switch (rank) {
            case 1 -> "A";
            case 11 -> "Q";
            case 12 -> "J";
            case 13 -> "K";
            default -> String.valueOf(rank);
        };

        String suitName = switch (suit) {
            case HEARTS -> "H";
            case CLUBS -> "C";
            case SPADES -> "S";
            case DIAMONDS -> "D";
        };
        return rankName + suitName;
    }

    private void loadImage(String imagePath) {
        image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath)));
    }

    public Image getImage() {
        return image;
    }

    public static boolean isCardClosed(Image image){
        return image.equals(ofClosedCard().getImage());
    }

    public static boolean isCardMissing(Image image){
        return image.equals(ofNoCard().getImage());
    }
}
