package GameClass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    private List<Card> cards;

    public Deck() {
        cards = new ArrayList<>();
        String[] suits = {"Hearts", "Diamonds", "Clubs", "Spades"};
        String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};

        for (String suit : suits) {
            for (String rank : ranks) {
                cards.add(new Card(suit, rank));
            }
        }
        Collections.shuffle(cards); // 카드 섞기
    }

    public Card drawCard() {
        if (cards.isEmpty()) {
            return null; // 카드가 없을 경우
        }
        return cards.remove(cards.size() - 1); // 마지막 카드를 뽑음
    }
}

