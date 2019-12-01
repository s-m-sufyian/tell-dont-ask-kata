package it.gabrieletondi.telldontaskkata.useCase;

import static it.gabrieletondi.telldontaskkata.useCase.OrderBuilder.anOrder;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import it.gabrieletondi.telldontaskkata.domain.Order;
import it.gabrieletondi.telldontaskkata.domain.OrderStatus;
import it.gabrieletondi.telldontaskkata.doubles.TestOrderRepository;
import org.junit.Test;

public class OrderApprovalUseCaseTest {

  private final TestOrderRepository orderRepository = new TestOrderRepository();
  private final OrderApprovalUseCase useCase = new OrderApprovalUseCase(orderRepository);

  @Test
  public void approvedExistingOrder() throws Exception {
    Order initialOrder = anOrder().with(OrderStatus.CREATED).build();
    orderRepository.add(initialOrder);

    OrderApprovalRequest request = new ApproveOrderRequest(1);

    useCase.run(request);
    final Order savedOrder = orderRepository.getSavedOrder();
    assertTrue(savedOrder.hasStatus(OrderStatus.APPROVED));
  }

  @Test
  public void rejectedExistingOrder() throws Exception {
    Order initialOrder = anOrder().with(OrderStatus.CREATED).build();
    orderRepository.add(initialOrder);

    OrderApprovalRequest request = new RejectOrderRequest(1);

    useCase.run(request);

    final Order savedOrder = orderRepository.getSavedOrder();
    assertTrue(savedOrder.hasStatus(OrderStatus.REJECTED));
  }

  @Test(expected = RejectedOrderCannotBeApprovedException.class)
  public void cannotApproveRejectedOrder() throws Exception {
    Order initialOrder = anOrder().with(OrderStatus.REJECTED).build();
    orderRepository.add(initialOrder);

    OrderApprovalRequest request = new ApproveOrderRequest(1);

    useCase.run(request);

    assertThat(orderRepository.getSavedOrder(), is(nullValue()));
  }

  @Test(expected = ApprovedOrderCannotBeRejectedException.class)
  public void cannotRejectApprovedOrder() throws Exception {
    Order initialOrder = anOrder().with(OrderStatus.APPROVED).build();
    orderRepository.add(initialOrder);

    OrderApprovalRequest request = new RejectOrderRequest(1);

    useCase.run(request);

    assertThat(orderRepository.getSavedOrder(), is(nullValue()));
  }

  @Test(expected = ShippedOrdersCannotBeChangedException.class)
  public void shippedOrdersCannotBeApproved() throws Exception {
    Order initialOrder = anOrder().with(OrderStatus.SHIPPED).build();
    orderRepository.add(initialOrder);

    OrderApprovalRequest request = new ApproveOrderRequest(1);

    useCase.run(request);

    assertThat(orderRepository.getSavedOrder(), is(nullValue()));
  }

  @Test(expected = ShippedOrdersCannotBeChangedException.class)
  public void shippedOrdersCannotBeRejected() throws Exception {
    Order initialOrder = anOrder().with(OrderStatus.SHIPPED).build();
    orderRepository.add(initialOrder);

    OrderApprovalRequest request = new RejectOrderRequest(1);

    useCase.run(request);

    assertThat(orderRepository.getSavedOrder(), is(nullValue()));
  }
}
