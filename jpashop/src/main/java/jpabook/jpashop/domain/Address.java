package jpabook.jpashop.domain;


import lombok.Getter;

import javax.persistence.Embeddable;

@Embeddable // JPA의 내장 타입이므로.
@Getter
public class Address {

    private String city;
    private String street;
    private String zipcode;

    // JPA가 객체를 생성할 때 리플렉션이나 프록시같은 기술을 사용해야 하는 경우가 많다.
    // 기본 생성자가 없으면 사용을 못하기 때문에, 기본 생성자를 만들어줘야 한다.
    // 다만, public으로 두면 사람들이 많이 호출할 수 있으므로 protected로 둔다.
    protected Address() {
    }

    // 값 타입은 변경이 불가능하게 설계되어야 한다. Setter 대신 생성자로 값을 넣어준다.
    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}
