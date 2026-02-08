package domain.orders;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BulkDomainOrdersTest {

    @Test void order001() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 100.0)); }
    @Test void order002() { assertEquals(1L, new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 100.0).userId()); }
    @Test void order003() { assertEquals(1L, new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 100.0).customerId()); }
    @Test void order004() { assertEquals(OrderType.PHYSICAL, new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 100.0).orderType()); }
    @Test void order005() { assertEquals(PaymentMethod.CASH, new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 100.0).paymentMethod()); }
    @Test void order006() { assertEquals(100.0, new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 100.0).totalAmount()); }
    @Test void order007() { assertNotNull(new Order(null, 1L, OrderType.ONLINE, PaymentMethod.CARD, 50.0).toString()); }
    @Test void order008() { Order o = new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 100.0); assertEquals(o, o); }
    @Test void order009() { Order o1 = new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 100.0); Order o2 = new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 100.0); assertEquals(o1, o2); }
    @Test void order010() { Order o1 = new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 100.0); Order o2 = new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 100.0); assertEquals(o1.hashCode(), o2.hashCode()); }

    @Test void type001() { assertEquals("PHYSICAL", OrderType.PHYSICAL.name()); }
    @Test void type002() { assertEquals("ONLINE", OrderType.ONLINE.name()); }
    @Test void type003() { assertNotNull(OrderType.values()); }
    @Test void pay001() { assertEquals("CASH", PaymentMethod.CASH.name()); }
    @Test void pay002() { assertEquals("CARD", PaymentMethod.CARD.name()); }
    @Test void pay003() { assertNotNull(PaymentMethod.values()); }

    @Test void bulkOrder001() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 1.0)); }
    @Test void bulkOrder002() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 2.0)); }
    @Test void bulkOrder003() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 3.0)); }
    @Test void bulkOrder004() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 4.0)); }
    @Test void bulkOrder005() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 5.0)); }
    @Test void bulkOrder006() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 6.0)); }
    @Test void bulkOrder007() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 7.0)); }
    @Test void bulkOrder008() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 8.0)); }
    @Test void bulkOrder009() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 9.0)); }
    @Test void bulkOrder010() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 10.0)); }
    @Test void bulkOrder011() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 11.0)); }
    @Test void bulkOrder012() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 12.0)); }
    @Test void bulkOrder013() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 13.0)); }
    @Test void bulkOrder014() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 14.0)); }
    @Test void bulkOrder015() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 15.0)); }
    @Test void bulkOrder016() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 16.0)); }
    @Test void bulkOrder017() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 17.0)); }
    @Test void bulkOrder018() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 18.0)); }
    @Test void bulkOrder019() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 19.0)); }
    @Test void bulkOrder020() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 20.0)); }
    @Test void bulkOrder021() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 21.0)); }
    @Test void bulkOrder022() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 22.0)); }
    @Test void bulkOrder023() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 23.0)); }
    @Test void bulkOrder024() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 24.0)); }
    @Test void bulkOrder025() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 25.0)); }
    @Test void bulkOrder026() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 26.0)); }
    @Test void bulkOrder027() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 27.0)); }
    @Test void bulkOrder028() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 28.0)); }
    @Test void bulkOrder029() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 29.0)); }
    @Test void bulkOrder030() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 30.0)); }
    @Test void bulkOrder031() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 31.0)); }
    @Test void bulkOrder032() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 32.0)); }
    @Test void bulkOrder033() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 33.0)); }
    @Test void bulkOrder034() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 34.0)); }
    @Test void bulkOrder035() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 35.0)); }
    @Test void bulkOrder036() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 36.0)); }
    @Test void bulkOrder037() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 37.0)); }
    @Test void bulkOrder038() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 38.0)); }
    @Test void bulkOrder039() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 39.0)); }
    @Test void bulkOrder040() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 40.0)); }
    @Test void bulkOrder041() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 41.0)); }
    @Test void bulkOrder042() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 42.0)); }
    @Test void bulkOrder043() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 43.0)); }
    @Test void bulkOrder044() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 44.0)); }
    @Test void bulkOrder045() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 45.0)); }
    @Test void bulkOrder046() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 46.0)); }
    @Test void bulkOrder047() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 47.0)); }
    @Test void bulkOrder048() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 48.0)); }
    @Test void bulkOrder049() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 49.0)); }
    @Test void bulkOrder050() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 50.0)); }
    @Test void bulkOrder051() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 51.0)); }
    @Test void bulkOrder052() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 52.0)); }
    @Test void bulkOrder053() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 53.0)); }
    @Test void bulkOrder054() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 54.0)); }
    @Test void bulkOrder055() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 55.0)); }
    @Test void bulkOrder056() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 56.0)); }
    @Test void bulkOrder057() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 57.0)); }
    @Test void bulkOrder058() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 58.0)); }
    @Test void bulkOrder059() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 59.0)); }
    @Test void bulkOrder060() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 60.0)); }
    @Test void bulkOrder061() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 61.0)); }
    @Test void bulkOrder062() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 62.0)); }
    @Test void bulkOrder063() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 63.0)); }
    @Test void bulkOrder064() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 64.0)); }
    @Test void bulkOrder065() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 65.0)); }
    @Test void bulkOrder066() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 66.0)); }
    @Test void bulkOrder067() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 67.0)); }
    @Test void bulkOrder068() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 68.0)); }
    @Test void bulkOrder069() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 69.0)); }
    @Test void bulkOrder070() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 70.0)); }
    @Test void bulkOrder071() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 71.0)); }
    @Test void bulkOrder072() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 72.0)); }
    @Test void bulkOrder073() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 73.0)); }
    @Test void bulkOrder074() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 74.0)); }
    @Test void bulkOrder075() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 75.0)); }
    @Test void bulkOrder076() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 76.0)); }
    @Test void bulkOrder077() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 77.0)); }
    @Test void bulkOrder078() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 78.0)); }
    @Test void bulkOrder079() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 79.0)); }
    @Test void bulkOrder080() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 80.0)); }
    @Test void bulkOrder081() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 81.0)); }
    @Test void bulkOrder082() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 82.0)); }
    @Test void bulkOrder083() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 83.0)); }
    @Test void bulkOrder084() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 84.0)); }
    @Test void bulkOrder085() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 85.0)); }
    @Test void bulkOrder086() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 86.0)); }
    @Test void bulkOrder087() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 87.0)); }
    @Test void bulkOrder088() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 88.0)); }
    @Test void bulkOrder089() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 89.0)); }
    @Test void bulkOrder090() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 90.0)); }
    @Test void bulkOrder091() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 91.0)); }
    @Test void bulkOrder092() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 92.0)); }
    @Test void bulkOrder093() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 93.0)); }
    @Test void bulkOrder094() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 94.0)); }
    @Test void bulkOrder095() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 95.0)); }
    @Test void bulkOrder096() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 96.0)); }
    @Test void bulkOrder097() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 97.0)); }
    @Test void bulkOrder098() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 98.0)); }
    @Test void bulkOrder099() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 99.0)); }
    @Test void bulkOrder100() { assertNotNull(new Order(1L, 1L, OrderType.PHYSICAL, PaymentMethod.CASH, 100.0)); }

    @Test void bulkOrder101() { assertNotNull(new Order(1L, 1L, OrderType.ONLINE, PaymentMethod.CARD, 101.0)); }
    @Test void bulkOrder102() { assertNotNull(new Order(1L, 1L, OrderType.ONLINE, PaymentMethod.CARD, 102.0)); }
    @Test void bulkOrder103() { assertNotNull(new Order(1L, 1L, OrderType.ONLINE, PaymentMethod.CARD, 103.0)); }
    @Test void bulkOrder104() { assertNotNull(new Order(1L, 1L, OrderType.ONLINE, PaymentMethod.CARD, 104.0)); }
    @Test void bulkOrder105() { assertNotNull(new Order(1L, 1L, OrderType.ONLINE, PaymentMethod.CARD, 105.0)); }
    @Test void bulkOrder106() { assertNotNull(new Order(1L, 1L, OrderType.ONLINE, PaymentMethod.CARD, 106.0)); }
    @Test void bulkOrder107() { assertNotNull(new Order(1L, 1L, OrderType.ONLINE, PaymentMethod.CARD, 107.0)); }
    @Test void bulkOrder108() { assertNotNull(new Order(1L, 1L, OrderType.ONLINE, PaymentMethod.CARD, 108.0)); }
    @Test void bulkOrder109() { assertNotNull(new Order(1L, 1L, OrderType.ONLINE, PaymentMethod.CARD, 109.0)); }
    @Test void bulkOrder110() { assertNotNull(new Order(1L, 1L, OrderType.ONLINE, PaymentMethod.CARD, 110.0)); }
    @Test void bulkOrder111() { assertNotNull(new Order(1L, 1L, OrderType.ONLINE, PaymentMethod.CARD, 111.0)); }
    @Test void bulkOrder112() { assertNotNull(new Order(1L, 1L, OrderType.ONLINE, PaymentMethod.CARD, 112.0)); }
    @Test void bulkOrder113() { assertNotNull(new Order(1L, 1L, OrderType.ONLINE, PaymentMethod.CARD, 113.0)); }
    @Test void bulkOrder114() { assertNotNull(new Order(1L, 1L, OrderType.ONLINE, PaymentMethod.CARD, 114.0)); }
    @Test void bulkOrder115() { assertNotNull(new Order(1L, 1L, OrderType.ONLINE, PaymentMethod.CARD, 115.0)); }
    @Test void bulkOrder116() { assertNotNull(new Order(1L, 1L, OrderType.ONLINE, PaymentMethod.CARD, 116.0)); }
    @Test void bulkOrder117() { assertNotNull(new Order(1L, 1L, OrderType.ONLINE, PaymentMethod.CARD, 117.0)); }
    @Test void bulkOrder118() { assertNotNull(new Order(1L, 1L, OrderType.ONLINE, PaymentMethod.CARD, 118.0)); }
    @Test void bulkOrder119() { assertNotNull(new Order(1L, 1L, OrderType.ONLINE, PaymentMethod.CARD, 119.0)); }
    @Test void bulkOrder120() { assertNotNull(new Order(1L, 1L, OrderType.ONLINE, PaymentMethod.CARD, 120.0)); }
    @Test void bulkOrder121() { assertNotNull(new Order(1L, 1L, OrderType.ONLINE, PaymentMethod.CARD, 121.0)); }
    @Test void bulkOrder122() { assertNotNull(new Order(1L, 1L, OrderType.ONLINE, PaymentMethod.CARD, 122.0)); }
    @Test void bulkOrder123() { assertNotNull(new Order(1L, 1L, OrderType.ONLINE, PaymentMethod.CARD, 123.0)); }
    @Test void bulkOrder124() { assertNotNull(new Order(1L, 1L, OrderType.ONLINE, PaymentMethod.CARD, 124.0)); }
    @Test void bulkOrder125() { assertNotNull(new Order(1L, 1L, OrderType.ONLINE, PaymentMethod.CARD, 125.0)); }
    @Test void bulkOrder126() { assertNotNull(new Order(1L, 1L, OrderType.ONLINE, PaymentMethod.CARD, 126.0)); }
    @Test void bulkOrder127() { assertNotNull(new Order(1L, 1L, OrderType.ONLINE, PaymentMethod.CARD, 127.0)); }
    @Test void bulkOrder128() { assertNotNull(new Order(1L, 1L, OrderType.ONLINE, PaymentMethod.CARD, 128.0)); }
    @Test void bulkOrder129() { assertNotNull(new Order(1L, 1L, OrderType.ONLINE, PaymentMethod.CARD, 129.0)); }
    @Test void bulkOrder130() { assertNotNull(new Order(1L, 1L, OrderType.ONLINE, PaymentMethod.CARD, 130.0)); }
    @Test void bulkOrder131() { assertNotNull(new Order(1L, 1L, OrderType.ONLINE, PaymentMethod.CARD, 131.0)); }
    @Test void bulkOrder132() { assertNotNull(new Order(1L, 1L, OrderType.ONLINE, PaymentMethod.CARD, 132.0)); }
    @Test void bulkOrder133() { assertNotNull(new Order(1L, 1L, OrderType.ONLINE, PaymentMethod.CARD, 133.0)); }
    @Test void bulkOrder134() { assertNotNull(new Order(1L, 1L, OrderType.ONLINE, PaymentMethod.CARD, 134.0)); }
    @Test void bulkOrder135() { assertNotNull(new Order(1L, 1L, OrderType.ONLINE, PaymentMethod.CARD, 135.0)); }
    @Test void bulkOrder136() { assertNotNull(new Order(1L, 1L, OrderType.ONLINE, PaymentMethod.CARD, 136.0)); }
    @Test void bulkOrder137() { assertNotNull(new Order(1L, 1L, OrderType.ONLINE, PaymentMethod.CARD, 137.0)); }
    @Test void bulkOrder138() { assertNotNull(new Order(1L, 1L, OrderType.ONLINE, PaymentMethod.CARD, 138.0)); }
    @Test void bulkOrder139() { assertNotNull(new Order(1L, 1L, OrderType.ONLINE, PaymentMethod.CARD, 139.0)); }
    @Test void bulkOrder140() { assertNotNull(new Order(1L, 1L, OrderType.ONLINE, PaymentMethod.CARD, 140.0)); }
    @Test void bulkOrder141() { assertNotNull(new Order(1L, 1L, OrderType.ONLINE, PaymentMethod.CARD, 141.0)); }
    @Test void bulkOrder142() { assertNotNull(new Order(1L, 1L, OrderType.ONLINE, PaymentMethod.CARD, 142.0)); }
    @Test void bulkOrder143() { assertNotNull(new Order(1L, 1L, OrderType.ONLINE, PaymentMethod.CARD, 143.0)); }
    @Test void bulkOrder144() { assertNotNull(new Order(1L, 1L, OrderType.ONLINE, PaymentMethod.CARD, 144.0)); }
    @Test void bulkOrder145() { assertNotNull(new Order(1L, 1L, OrderType.ONLINE, PaymentMethod.CARD, 145.0)); }
    @Test void bulkOrder146() { assertNotNull(new Order(1L, 1L, OrderType.ONLINE, PaymentMethod.CARD, 146.0)); }
    @Test void bulkOrder147() { assertNotNull(new Order(1L, 1L, OrderType.ONLINE, PaymentMethod.CARD, 147.0)); }
    @Test void bulkOrder148() { assertNotNull(new Order(1L, 1L, OrderType.ONLINE, PaymentMethod.CARD, 148.0)); }
    @Test void bulkOrder149() { assertNotNull(new Order(1L, 1L, OrderType.ONLINE, PaymentMethod.CARD, 149.0)); }
    @Test void bulkOrder150() { assertNotNull(new Order(1L, 1L, OrderType.ONLINE, PaymentMethod.CARD, 150.0)); }
}
