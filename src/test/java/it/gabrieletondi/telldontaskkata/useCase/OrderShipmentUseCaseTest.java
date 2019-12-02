package it.gabrieletondi.telldontaskkata.useCase;

import static it.gabrieletondi.telldontaskkata.useCase.OrderBuilder.anOrder;
import static junit.framework.TestCase.assertTrue;

import it.gabrieletondi.telldontaskkata.domain.Order;
import it.gabrieletondi.telldontaskkata.domain.Shipped;
import it.gabrieletondi.telldontaskkata.doubles.TestOrderRepository;
import it.gabrieletondi.telldontaskkata.doubles.TestShipmentService;
import it.gabrieletondi.telldontaskkata.useCase.shipment.OrderShipmentRequest;
import it.gabrieletondi.telldontaskkata.useCase.shipment.OrderShipmentUseCase;
import it.gabrieletondi.telldontaskkata.useCase.shipment.invariants.OrderCannotBeShippedTwiceException;
import it.gabrieletondi.telldontaskkata.useCase.shipment.invariants.OrderNotReadyForShippmentException;
import org.junit.Test;

public class OrderShipmentUseCaseTest {

  private final TestOrderRepository orderRepository = new TestOrderRepository();
  private final TestShipmentService shipmentService = new TestShipmentService();
  private final OrderShipmentUseCase shipment = new OrderShipmentUseCase(orderRepository, shipmentService);
  private int anOrderId = 2;

  @Test
  public void shipApprovedOrder() throws Exception {
    final Order initialOrder = anOrder().with(anOrderId).thatIsApproved().build();
    orderRepository.add(initialOrder);

    shipment.run(new OrderShipmentRequest(anOrderId));

    assertTrue(orderRepository.savedOrderIs(new Shipped()));
    assertTrue(shipmentService.shippedOrderIs(initialOrder));
  }

  @Test(expected = OrderNotReadyForShippmentException.class)
  public void createdOrdersCannotBeShipped() throws Exception {
    orderRepository.add(anOrder().thatIsCreated().build());

    shipment.run(new OrderShipmentRequest(1));

    assertTrue(orderRepository.orderIsNotSaved());
    assertTrue(shipmentService.orderIsNotShipped());
  }

  @Test(expected = OrderNotReadyForShippmentException.class)
  public void rejectedOrdersCannotBeShipped() throws Exception {
    orderRepository.add(anOrder().thatIsRejected().build());

    shipment.run(new OrderShipmentRequest(1));

    assertTrue(orderRepository.orderIsNotSaved());
    assertTrue(shipmentService.orderIsNotShipped());
  }

  @Test(expected = OrderCannotBeShippedTwiceException.class)
  public void shippedOrdersCannotBeShippedAgain() throws Exception {
    orderRepository.add(anOrder().thatIsShipped().build());

    shipment.run(new OrderShipmentRequest(1));

    assertTrue(orderRepository.orderIsNotSaved());
    assertTrue(shipmentService.orderIsNotShipped());
  }
}
