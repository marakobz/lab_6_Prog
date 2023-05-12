package models;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class Ticket implements Comparable<Ticket>, Serializable {
    public static final int MIN_ID = 0;
    public static final int MIN_PRICE = 0;
    public static final long MIN_DISCOUNT = 0;
    public static final long MAX_DISCOUNT = 100;


    private int id; //Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private java.time.LocalDate creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private int price; //Значение поля должно быть больше 0
    private long discount; //Значение поля должно быть больше 0, Максимальное значение поля: 100
    private Boolean refundable; //Поле не может быть null
    private TicketType type; //Поле может быть null
    private Person person; //Поле не может быть null

    public Ticket(int id,
                  String name,
                  Coordinates coordinates,
                  LocalDate creationDate,
                  int price,
                  long discount,
                  Boolean refundable,
                  TicketType type,
                  Person person) {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.price = price;
        this.discount = discount;
        this.refundable = refundable;
        this.type = type;
        this.person = person;
    }

    @Override
    public int compareTo(Ticket ticket) {
        return getName().compareTo(ticket.getName());
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public int getPrice() {
        return price;
    }

    public long getDiscount() {
        return discount;
    }

    public Boolean getRefundable() {
        return refundable;
    }

    public TicketType getType() {
        return type;
    }

    public Person getPerson() {
        return person;
    }


    @Override
    public String toString() {
        String info = "";
        info += "Ticket №" + id;
        info += " (added in " + creationDate.toString() + ")";
        info += "\n Name: " + name;
        info += "\n Place: " + coordinates;
        info += "\n Price: " + price;
        info += "\n Discount: " + discount;
        info += "\n Refund: " + refundable;
        info += "\n Ticket type: " + type;
        info += "\n Person: " + person;
        return info;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getCoordinates(), getCreationDate(), getPrice(), getDiscount(), getRefundable(), getType(), getPerson());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof Ticket) {
            Ticket ticketObj = (Ticket) obj;
            return (
                    name.equals(ticketObj.getName()) &&
                            coordinates.equals(ticketObj.getCoordinates()) &&
                            creationDate.equals(ticketObj.getCreationDate()) &&
                            (price == ticketObj.getPrice()) &&
                            refundable.equals(ticketObj.getRefundable()) &&
                            type.equals(ticketObj.getType()) &&
                            person.equals(ticketObj.getPerson()));
        }
        return false;
    }

}
