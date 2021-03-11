package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.support.Querydsl;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {
    private final EntityManager em;

    public void save(Order order){
        em.persist(order);
    }

    public Order findOne(Long id){
        return em.find(Order.class, id);
    }

    /** 검색 기능 : 동적 쿼리가 들어가야 해서 복잡하다
     *  동적 쿼리 - where문의 내용이 필요에 따라 바뀔 수 있는 형식의 쿼리
     */
    // 상태체크 하지말고 주문이든 취소든 다 들고와 라고 한다면 쿼리가 바뀌어야 한다 -> 동적 쿼리가 되어야 한다
    // 동적 쿼리의 지옥이 시작됨! mybatis는 동적 쿼리를 xml로 조작할 수 있는 설정이 잘 되어 있다. 하지만 JPA는...

/*    public List<Order> findAll(OrderSearch orderSearch){ // 나의 의문점. 왜 Order만 Order.class 해주고 Member는 없는지? 일대다 매핑관계 때문인가?
        return em.createQuery("select o from Order o join o.member m" +
                        " where o.status = :status" + // 파라미터 바인딩
                        " and m.name like :name", Order.class) // 값이 없으면 이런 문장이 있으면 안될 것이다
                        .setParameter("status", orderSearch.getOrderStatus()) // 파라미터 없이 무조건 다가져와(select *)도 할 수 있다
                        .setParameter("name", orderSearch.getMemberName())
                        .setMaxResults(1000)    // 최대 1000건만 가능하도록
                        .getResultList();
    }*/

    /** 그냥 JPQL */
    // 1. JPQL 문자열로 생성 -> 번거롭고 실수로 인한 버그가 충분히 발생할 수 있다.
/*    public List<Order> findAllByString(OrderSearch orderSearch){
        String jpql = "select o from Order o join o.member m";

        // 이제 이것을 동적으로 생성해야 한다..?! -> 실무에서 잘 안하는 방식
        boolean isFirstCondition = true;
        // 주문 상태 검색
        if(orderSearch.getOrderStatus() != null){
            if (isFirstCondition){
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += "and";
            }
            jpql += " o.status = :status";
        }
        // 회원 이름 검색
        if(StringUtils.hasText(orderSearch.getMemberName())){
            if(isFirstCondition){
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " m.name like :name";
        }

        TypedQuery<Order> query = em.createQuery(jpql, Order.class)
                .setMaxResults(1000);

        if (orderSearch.getOrderStatus() != null){ // order의 상태가 있으면
            query = query.setParameter("status", orderSearch.getOrderStatus());
        }
        if (StringUtils.hasText(orderSearch.getMemberName())){
            query = query.setParameter("name", orderSearch.getMemberName());
        }
        return query.getResultList();
    }*/

    /**
     * JPA Criteria 로 해결 -> 이것도 권장 X, 실무에서 쓰지 않는다, 단 JPA 표준 스펙임
     */
    // 2. JPA가 제공하는 표준, 동적 쿼리를 빌드해주는 JPQL을 자바 코드로 작성할 수 있다
    // ※ 치명적인 단점 : 머리로만 코딩한 것임 (유지보수성이 zero, 어떤 쿼리가 생성될지 머리에 떠오르지 않는다)
    public List<Order> findAllByCriteria(OrderSearch orderSearch){
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class); // 시작하는 엔티티
        Root<Order> o = cq.from(Order.class); // alias 잡기
        Join<Object, Object> m = o.join("member", JoinType.INNER);

        // 동적 쿼리에 대한 컨디션 조합을 Predicate를 가지고 아주 깔끔하게 만들 수 있다.
        List<Predicate> criteria = new ArrayList<>();

        // 주문 상태 검색
        if (orderSearch.getOrderStatus() != null){
            // Predicate 자체가 조건이 됨
            Predicate status = cb.equal(o.get("status"), orderSearch.getOrderStatus());
            criteria.add(status);
        }
        // 회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())){
            // Predicate 자체가 조건이 됨
            Predicate name = cb.like(m.<String>get("name"), "%" + orderSearch.getMemberName() + "%");
            criteria.add(name);
        }

        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000);
        return query.getResultList();
    }

    // 어떻게 하면 컴파일 시점에 잘 해결할 수 있을까? -> 고민해서 나온 라이브러리
    /** 3. ★★★★★★ QueryDSL!!!!! ★★★★★★
     *  실무 할 때에는 동적 쿼리 때문 + 복잡한 JPQL 해결하기 위해서
     *  SpringBoot + spring-data-jpa + QueryDSL
     *  : 실무에서 생산성도 극대화하고 코드도 아름답게하며 컴파일 시점에 문법적 오류도 잡을 수 있다!
     */
/*
    public List<Order> findAll(OrderSearch orderSearch){
        QOrder order = QOrder.order;
        QMember member = QMember.member;

        return query
                .selet(order)
                .from(order)
                .join(order.member, member)
                .where(statusEq(orderSearch.getOrderStatus())),
                        nameLike(orderSearch.getMemberName()))
                .limit(1000)
                .fetch();
    }
    private BooleanExpression statusEq(OrderStatus statusCond){
        if(statusCond == null){
            return null;
        }
        return order.status.eq(statusCond);
    }

    private Boolean Expression nameLike(String nameCond){
        if (!StringUtils.hasText(nameCond)){
            return null;
        }
        return member.name.like(nameCond);
    }
*/

}
