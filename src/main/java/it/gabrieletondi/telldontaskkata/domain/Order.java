package it.gabrieletondi.telldontaskkata.domain;

import static it.gabrieletondi.telldontaskkata.domain.OrderStatus.APPROVED;
import static it.gabrieletondi.telldontaskkata.domain.OrderStatus.CREATED;
import static it.gabrieletondi.telldontaskkata.domain.OrderStatus.REJECTED;
import static it.gabrieletondi.telldontaskkata.domain.OrderStatus.SHIPPED;

import it.gabrieletondi.telldontaskkata.useCase.invariants.ApprovedOrderCannotBeRejectedException;
import it.gabrieletondi.telldontaskkata.useCase.invariants.OrderCannotBeShippedTwiceException;
import it.gabrieletondi.telldontaskkata.useCase.invariants.OrderNotReadyForShippmentException;
import it.gabrieletondi.telldontaskkata.useCase.invariants.RejectedOrderCannotBeApprovedException;
import it.gabrieletondi.telldontaskkata.useCase.invariants.ShippedOrdersCannotBeChangedException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class Order {

  private int id;
  private OrderStatus status;
  private List<OrderItem> items;
  private String currency;
  private BigDecimal total;
  private BigDecimal tax;

  public Order(int id, OrderStatus status, List<OrderItem> items, String currency, BigDecimal total, BigDecimal tax) {
    this.id = id;
    this.status = status;
    this.items = items;
    this.currency = currency;
    this.total = total;
    this.tax = tax;
  }

  public static Order withoutOrderItems() {
    return new Order(1, OrderStatus.CREATED, new ArrayList<>(), "EUR", new BigDecimal("0.00"),
        new BigDecimal("0.00"));
  }

  public boolean hasId(int orderId) {
    return id == orderId;
  }

  public void assertCanBeShipped() {
    assertNotShippedAlready();
    assertReadyForShipment();
  }

  public void markAsRejected() {
    assertNotTryingToChangeShippedOrder();
    assertNotRejectingApprovedOrder();
    changeStatusTo(REJECTED);
  }

  public void markAsApproved() {
    assertNotTryingToChangeShippedOrder();
    assertNotApprovingRejectedOrder();
    changeStatusTo(APPROVED);
  }

  public void markAsShipped() {
    changeStatusTo(SHIPPED);
  }

  public void addOrderItemFor(Product product, int quantity) {
    add(OrderItem.forA(product, quantity));
  }

  public boolean hasStatus(OrderStatus thatStatus) {
    return this.status == thatStatus;
  }

  private void add(OrderItem orderItem) {
    items.add(orderItem);
    total = orderItem.addTaxedAmountTo(total);
    tax = orderItem.addTaxAmountTo(tax);
  }

  private void assertNotShippedAlready() {
    if (shippedAlready()) {
      throw new OrderCannotBeShippedTwiceException();
    }
  }

  private void assertReadyForShipment() {
    if (notReadyForShipment()) {
      throw new OrderNotReadyForShippmentException();
    }
  }

  private boolean notReadyForShipment() {
    return is(CREATED) || is(REJECTED);
  }

  private void changeStatusTo(OrderStatus approved) {
    this.status = approved;
  }

  private boolean shippedAlready() {
    return is(SHIPPED);
  }

  private void assertNotApprovingRejectedOrder() {
    if (approvingRejectedOrder()) {
      throw new RejectedOrderCannotBeApprovedException();
    }
  }

  private boolean approvingRejectedOrder() {
    return is(REJECTED);
  }

  private void assertNotRejectingApprovedOrder() {
    if (rejectingApprovedOrder()) {
      throw new ApprovedOrderCannotBeRejectedException();
    }
  }

  private boolean rejectingApprovedOrder() {
    return is(APPROVED);
  }

  private void assertNotTryingToChangeShippedOrder() {
    if (tryingToChangeShippedOrder()) {
      throw new ShippedOrdersCannotBeChangedException();
    }
  }

  private boolean tryingToChangeShippedOrder() {
    return is(SHIPPED);
  }

  private boolean is(OrderStatus thatStatus) {
    return this.status == thatStatus;
  }
}
