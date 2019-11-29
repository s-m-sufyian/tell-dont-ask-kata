package it.gabrieletondi.telldontaskkata.useCase;

import static it.gabrieletondi.telldontaskkata.domain.OrderStatus.APPROVED;
import static it.gabrieletondi.telldontaskkata.domain.OrderStatus.REJECTED;

import it.gabrieletondi.telldontaskkata.domain.Order;

class OrderApprovalRequest {

  private int orderId;
  private boolean approved;

  void assertNotRejectingApprovedOrder(Order order) {
    if (rejectingApprovedOrder(order)) {
      throw new ApprovedOrderCannotBeRejectedException();
    }
  }

  private boolean rejectingApprovedOrder(Order order) {
    return isNotApproved() && order.is(APPROVED);
  }

  boolean approvingRejectedOrder(Order order) {
    return approved && order.is(REJECTED);
  }

  private boolean isNotApproved() {
    return !approved;
  }

  int getOrderId() {
    return orderId;
  }

  void setOrderId(int orderId) {
    this.orderId = orderId;
  }

  boolean isApproved() {
    return approved;
  }

  void setApproved(boolean approved) {
    this.approved = approved;
  }
}
