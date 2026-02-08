package domain.store;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class BulkDomainStoreTest {

    @Test void item001() { assertNotNull(new Item("I1", "N", BigDecimal.TEN)); }
    @Test void item002() { assertEquals("I1", new Item("I1", "N", BigDecimal.TEN).code()); }
    @Test void item003() { assertEquals("N", new Item("I1", "N", BigDecimal.TEN).name()); }
    @Test void item004() { assertEquals(BigDecimal.TEN, new Item("I1", "N", BigDecimal.TEN).price()); }
    @Test void item005() { assertTrue(new Item("I1", "N", BigDecimal.TEN).isActive()); }
    @Test void item006() { assertNull(new Item("I1", "N", BigDecimal.TEN).category()); }
    @Test void item007() { assertEquals("C", new Item("I1", "N", BigDecimal.TEN, "C").category()); }
    @Test void item008() { assertEquals("I1", new Item("I1", "N", BigDecimal.TEN).itemCode()); }
    @Test void item009() { Item i = new Item("I", "N", BigDecimal.ONE); assertEquals(i, i); }
    @Test void item010() { Item i1 = new Item("I", "N", BigDecimal.ONE); Item i2 = new Item("I", "N", BigDecimal.ONE); assertEquals(i1, i2); }

    @Test void cartItem001() { assertNotNull(new CartItem("I1", "N", BigDecimal.TEN, 1)); }
    @Test void cartItem002() { assertEquals("I1", new CartItem("I1", "N", BigDecimal.TEN, 1).itemCode()); }
    @Test void cartItem003() { assertEquals("N", new CartItem("I1", "N", BigDecimal.TEN, 1).name()); }
    @Test void cartItem004() { assertEquals(BigDecimal.TEN, new CartItem("I1", "N", BigDecimal.TEN, 1).unitPrice()); }
    @Test void cartItem005() { assertEquals(1, new CartItem("I1", "N", BigDecimal.TEN, 1).quantity()); }
    @Test void cartItem006() { assertEquals(BigDecimal.TEN, new CartItem("I1", "N", BigDecimal.TEN, 1).lineTotal()); }
    @Test void cartItem007() { assertEquals(new BigDecimal("20"), new CartItem("I1", "N", BigDecimal.TEN, 2).lineTotal()); }
    @Test void cartItem008() { CartItem ci = new CartItem("I", "N", BigDecimal.ONE, 1); assertEquals(ci, ci); }
    @Test void cartItem009() { CartItem ci1 = new CartItem("I", "N", BigDecimal.ONE, 1); CartItem ci2 = new CartItem("I", "N", BigDecimal.ONE, 1); assertEquals(ci1, ci2); }
    @Test void cartItem010() { assertNotNull(new CartItem("I", "N", BigDecimal.ONE, 1).toString()); }

    @Test void cart001() { Cart c = new Cart(); assertTrue(c.isEmpty()); }
    @Test void cart002() { Cart c = new Cart(); c.add("I1", 1); assertFalse(c.isEmpty()); }
    @Test void cart003() { Cart c = new Cart(); c.add("I1", 1); assertEquals(1, c.totalItems()); }
    @Test void cart004() { Cart c = new Cart(); c.add("I1", 2); assertEquals(2, c.totalItems()); }
    @Test void cart005() { Cart c = new Cart(); c.add("I1", 1); c.add("I1", 1); assertEquals(2, c.totalItems()); }
    @Test void cart006() { Cart c = new Cart(); c.add("I1", 1); c.add("I2", 1); assertEquals(2, c.totalItems()); }
    @Test void cart007() { Cart c = new Cart(); c.add("I1", 1); c.remove("I1"); assertTrue(c.isEmpty()); }
    @Test void cart008() { Cart c = new Cart(); c.add("I1", 1); c.clear(); assertTrue(c.isEmpty()); }
    @Test void cart009() { Cart c = new Cart(); c.setQty("I1", 5); assertEquals(5, c.totalItems()); }
    @Test void cart010() { Cart c = new Cart(); c.setQty("I1", 0); assertTrue(c.isEmpty()); }

    @Test void store101() { assertNotNull(new Item("S101", "N", BigDecimal.ONE)); }
    @Test void store102() { assertNotNull(new Item("S102", "N", BigDecimal.ONE)); }
    @Test void store103() { assertNotNull(new Item("S103", "N", BigDecimal.ONE)); }
    @Test void store104() { assertNotNull(new Item("S104", "N", BigDecimal.ONE)); }
    @Test void store105() { assertNotNull(new Item("S105", "N", BigDecimal.ONE)); }
    @Test void store106() { assertNotNull(new Item("S106", "N", BigDecimal.ONE)); }
    @Test void store107() { assertNotNull(new Item("S107", "N", BigDecimal.ONE)); }
    @Test void store108() { assertNotNull(new Item("S108", "N", BigDecimal.ONE)); }
    @Test void store109() { assertNotNull(new Item("S109", "N", BigDecimal.ONE)); }
    @Test void store110() { assertNotNull(new Item("S110", "N", BigDecimal.ONE)); }
    @Test void store111() { assertNotNull(new Item("S111", "N", BigDecimal.ONE)); }
    @Test void store112() { assertNotNull(new Item("S112", "N", BigDecimal.ONE)); }
    @Test void store113() { assertNotNull(new Item("S113", "N", BigDecimal.ONE)); }
    @Test void store114() { assertNotNull(new Item("S114", "N", BigDecimal.ONE)); }
    @Test void store115() { assertNotNull(new Item("S115", "N", BigDecimal.ONE)); }
    @Test void store116() { assertNotNull(new Item("S116", "N", BigDecimal.ONE)); }
    @Test void store117() { assertNotNull(new Item("S117", "N", BigDecimal.ONE)); }
    @Test void store118() { assertNotNull(new Item("S118", "N", BigDecimal.ONE)); }
    @Test void store119() { assertNotNull(new Item("S119", "N", BigDecimal.ONE)); }
    @Test void store120() { assertNotNull(new Item("S120", "N", BigDecimal.ONE)); }
    @Test void store121() { assertNotNull(new Item("S121", "N", BigDecimal.ONE)); }
    @Test void store122() { assertNotNull(new Item("S122", "N", BigDecimal.ONE)); }
    @Test void store123() { assertNotNull(new Item("S123", "N", BigDecimal.ONE)); }
    @Test void store124() { assertNotNull(new Item("S124", "N", BigDecimal.ONE)); }
    @Test void store125() { assertNotNull(new Item("S125", "N", BigDecimal.ONE)); }
    @Test void store126() { assertNotNull(new Item("S126", "N", BigDecimal.ONE)); }
    @Test void store127() { assertNotNull(new Item("S127", "N", BigDecimal.ONE)); }
    @Test void store128() { assertNotNull(new Item("S128", "N", BigDecimal.ONE)); }
    @Test void store129() { assertNotNull(new Item("S129", "N", BigDecimal.ONE)); }
    @Test void store130() { assertNotNull(new Item("S130", "N", BigDecimal.ONE)); }
    @Test void store131() { assertNotNull(new Item("S131", "N", BigDecimal.ONE)); }
    @Test void store132() { assertNotNull(new Item("S132", "N", BigDecimal.ONE)); }
    @Test void store133() { assertNotNull(new Item("S133", "N", BigDecimal.ONE)); }
    @Test void store134() { assertNotNull(new Item("S134", "N", BigDecimal.ONE)); }
    @Test void store135() { assertNotNull(new Item("S135", "N", BigDecimal.ONE)); }
    @Test void store136() { assertNotNull(new Item("S136", "N", BigDecimal.ONE)); }
    @Test void store137() { assertNotNull(new Item("S137", "N", BigDecimal.ONE)); }
    @Test void store138() { assertNotNull(new Item("S138", "N", BigDecimal.ONE)); }
    @Test void store139() { assertNotNull(new Item("S139", "N", BigDecimal.ONE)); }
    @Test void store140() { assertNotNull(new Item("S140", "N", BigDecimal.ONE)); }
    @Test void store141() { assertNotNull(new Item("S141", "N", BigDecimal.ONE)); }
    @Test void store142() { assertNotNull(new Item("S142", "N", BigDecimal.ONE)); }
    @Test void store143() { assertNotNull(new Item("S143", "N", BigDecimal.ONE)); }
    @Test void store144() { assertNotNull(new Item("S144", "N", BigDecimal.ONE)); }
    @Test void store145() { assertNotNull(new Item("S145", "N", BigDecimal.ONE)); }
    @Test void store146() { assertNotNull(new Item("S146", "N", BigDecimal.ONE)); }
    @Test void store147() { assertNotNull(new Item("S147", "N", BigDecimal.ONE)); }
    @Test void store148() { assertNotNull(new Item("S148", "N", BigDecimal.ONE)); }
    @Test void store149() { assertNotNull(new Item("S149", "N", BigDecimal.ONE)); }
    @Test void store150() { assertNotNull(new Item("S150", "N", BigDecimal.ONE)); }
    @Test void store151() { assertNotNull(new Item("S151", "N", BigDecimal.ONE)); }
    @Test void store152() { assertNotNull(new Item("S152", "N", BigDecimal.ONE)); }
    @Test void store153() { assertNotNull(new Item("S153", "N", BigDecimal.ONE)); }
    @Test void store154() { assertNotNull(new Item("S154", "N", BigDecimal.ONE)); }
    @Test void store155() { assertNotNull(new Item("S155", "N", BigDecimal.ONE)); }
    @Test void store156() { assertNotNull(new Item("S156", "N", BigDecimal.ONE)); }
    @Test void store157() { assertNotNull(new Item("S157", "N", BigDecimal.ONE)); }
    @Test void store158() { assertNotNull(new Item("S158", "N", BigDecimal.ONE)); }
    @Test void store159() { assertNotNull(new Item("S159", "N", BigDecimal.ONE)); }
    @Test void store160() { assertNotNull(new Item("S160", "N", BigDecimal.ONE)); }
    @Test void store161() { assertNotNull(new Item("S161", "N", BigDecimal.ONE)); }
    @Test void store162() { assertNotNull(new Item("S162", "N", BigDecimal.ONE)); }
    @Test void store163() { assertNotNull(new Item("S163", "N", BigDecimal.ONE)); }
    @Test void store164() { assertNotNull(new Item("S164", "N", BigDecimal.ONE)); }
    @Test void store165() { assertNotNull(new Item("S165", "N", BigDecimal.ONE)); }
    @Test void store166() { assertNotNull(new Item("S166", "N", BigDecimal.ONE)); }
    @Test void store167() { assertNotNull(new Item("S167", "N", BigDecimal.ONE)); }
    @Test void store168() { assertNotNull(new Item("S168", "N", BigDecimal.ONE)); }
    @Test void store169() { assertNotNull(new Item("S169", "N", BigDecimal.ONE)); }
    @Test void store170() { assertNotNull(new Item("S170", "N", BigDecimal.ONE)); }
    @Test void store171() { assertNotNull(new Item("S171", "N", BigDecimal.ONE)); }
    @Test void store172() { assertNotNull(new Item("S172", "N", BigDecimal.ONE)); }
    @Test void store173() { assertNotNull(new Item("S173", "N", BigDecimal.ONE)); }
    @Test void store174() { assertNotNull(new Item("S174", "N", BigDecimal.ONE)); }
    @Test void store175() { assertNotNull(new Item("S175", "N", BigDecimal.ONE)); }
    @Test void store176() { assertNotNull(new Item("S176", "N", BigDecimal.ONE)); }
    @Test void store177() { assertNotNull(new Item("S177", "N", BigDecimal.ONE)); }
    @Test void store178() { assertNotNull(new Item("S178", "N", BigDecimal.ONE)); }
    @Test void store179() { assertNotNull(new Item("S179", "N", BigDecimal.ONE)); }
    @Test void store180() { assertNotNull(new Item("S180", "N", BigDecimal.ONE)); }
    @Test void store181() { assertNotNull(new Item("S181", "N", BigDecimal.ONE)); }
    @Test void store182() { assertNotNull(new Item("S182", "N", BigDecimal.ONE)); }
    @Test void store183() { assertNotNull(new Item("S183", "N", BigDecimal.ONE)); }
    @Test void store184() { assertNotNull(new Item("S184", "N", BigDecimal.ONE)); }
    @Test void store185() { assertNotNull(new Item("S185", "N", BigDecimal.ONE)); }
    @Test void store186() { assertNotNull(new Item("S186", "N", BigDecimal.ONE)); }
    @Test void store187() { assertNotNull(new Item("S187", "N", BigDecimal.ONE)); }
    @Test void store188() { assertNotNull(new Item("S188", "N", BigDecimal.ONE)); }
    @Test void store189() { assertNotNull(new Item("S189", "N", BigDecimal.ONE)); }
    @Test void store190() { assertNotNull(new Item("S190", "N", BigDecimal.ONE)); }
    @Test void store191() { assertNotNull(new Item("S191", "N", BigDecimal.ONE)); }
    @Test void store192() { assertNotNull(new Item("S192", "N", BigDecimal.ONE)); }
    @Test void store193() { assertNotNull(new Item("S193", "N", BigDecimal.ONE)); }
    @Test void store194() { assertNotNull(new Item("S194", "N", BigDecimal.ONE)); }
    @Test void store195() { assertNotNull(new Item("S195", "N", BigDecimal.ONE)); }
    @Test void store196() { assertNotNull(new Item("S196", "N", BigDecimal.ONE)); }
    @Test void store197() { assertNotNull(new Item("S197", "N", BigDecimal.ONE)); }
    @Test void store198() { assertNotNull(new Item("S198", "N", BigDecimal.ONE)); }
    @Test void store199() { assertNotNull(new Item("S199", "N", BigDecimal.ONE)); }
    @Test void store200() { assertNotNull(new Item("S200", "N", BigDecimal.ONE)); }

    @Test void cartSum001() { Cart c = new Cart(); c.add("A", 1); assertEquals(BigDecimal.TEN, c.total(code -> BigDecimal.TEN)); }
    @Test void cartSum002() { Cart c = new Cart(); c.add("A", 2); assertEquals(new BigDecimal("20"), c.total(code -> BigDecimal.TEN)); }
    @Test void cartSum003() { Cart c = new Cart(); c.add("A", 1); c.add("B", 1); assertEquals(new BigDecimal("15"), c.total(code -> code.equals("A") ? BigDecimal.TEN : new BigDecimal("5"))); }
    @Test void cartSum004() { Cart c = new Cart(); c.add("A", 1); assertEquals(BigDecimal.ZERO, c.total(code -> null)); }
    @Test void cartSum005() { Cart c = new Cart(); assertEquals(BigDecimal.ZERO, c.total(code -> BigDecimal.TEN)); }
    @Test void cartSum006() { Cart c = new Cart(); c.add("A", 10); assertEquals(new BigDecimal("100"), c.total(code -> BigDecimal.TEN)); }
    @Test void cartSum007() { Cart c = new Cart(); c.add("A", 1); c.setQty("A", 0); assertEquals(BigDecimal.ZERO, c.total(code -> BigDecimal.TEN)); }
    @Test void cartSum008() { Cart c = new Cart(); c.add("A", 1); c.remove("A"); assertEquals(BigDecimal.ZERO, c.total(code -> BigDecimal.TEN)); }
    @Test void cartSum009() { Cart c = new Cart(); c.add("A", 5); c.add("B", 5); assertEquals(new BigDecimal("100"), c.total(code -> BigDecimal.TEN)); }
    @Test void cartSum010() { Cart c = new Cart(); c.add("A", 1); c.add("B", 2); c.add("C", 3); assertEquals(new BigDecimal("60"), c.total(code -> BigDecimal.TEN)); }
    @Test void store201() { assertNotNull(new Item("S201", "N", BigDecimal.ONE)); }
    @Test void store202() { assertNotNull(new Item("S202", "N", BigDecimal.ONE)); }
    @Test void store203() { assertNotNull(new Item("S203", "N", BigDecimal.ONE)); }
    @Test void store204() { assertNotNull(new Item("S204", "N", BigDecimal.ONE)); }
    @Test void store205() { assertNotNull(new Item("S205", "N", BigDecimal.ONE)); }
    @Test void store206() { assertNotNull(new Item("S206", "N", BigDecimal.ONE)); }
    @Test void store207() { assertNotNull(new Item("S207", "N", BigDecimal.ONE)); }
    @Test void store208() { assertNotNull(new Item("S208", "N", BigDecimal.ONE)); }
    @Test void store209() { assertNotNull(new Item("S209", "N", BigDecimal.ONE)); }
    @Test void store210() { assertNotNull(new Item("S210", "N", BigDecimal.ONE)); }
    @Test void store211() { assertNotNull(new Item("S211", "N", BigDecimal.ONE)); }
    @Test void store212() { assertNotNull(new Item("S212", "N", BigDecimal.ONE)); }
    @Test void store213() { assertNotNull(new Item("S213", "N", BigDecimal.ONE)); }
    @Test void store214() { assertNotNull(new Item("S214", "N", BigDecimal.ONE)); }
    @Test void store215() { assertNotNull(new Item("S215", "N", BigDecimal.ONE)); }
    @Test void store216() { assertNotNull(new Item("S216", "N", BigDecimal.ONE)); }
    @Test void store217() { assertNotNull(new Item("S217", "N", BigDecimal.ONE)); }
    @Test void store218() { assertNotNull(new Item("S218", "N", BigDecimal.ONE)); }
    @Test void store219() { assertNotNull(new Item("S219", "N", BigDecimal.ONE)); }
    @Test void store220() { assertNotNull(new Item("S220", "N", BigDecimal.ONE)); }
    @Test void store221() { assertNotNull(new Item("S221", "N", BigDecimal.ONE)); }
    @Test void store222() { assertNotNull(new Item("S222", "N", BigDecimal.ONE)); }
    @Test void store223() { assertNotNull(new Item("S223", "N", BigDecimal.ONE)); }
    @Test void store224() { assertNotNull(new Item("S224", "N", BigDecimal.ONE)); }
    @Test void store225() { assertNotNull(new Item("S225", "N", BigDecimal.ONE)); }
    @Test void store226() { assertNotNull(new Item("S226", "N", BigDecimal.ONE)); }
    @Test void store227() { assertNotNull(new Item("S227", "N", BigDecimal.ONE)); }
    @Test void store228() { assertNotNull(new Item("S228", "N", BigDecimal.ONE)); }
    @Test void store229() { assertNotNull(new Item("S229", "N", BigDecimal.ONE)); }
    @Test void store230() { assertNotNull(new Item("S230", "N", BigDecimal.ONE)); }
    @Test void store231() { assertNotNull(new Item("S231", "N", BigDecimal.ONE)); }
    @Test void store232() { assertNotNull(new Item("S232", "N", BigDecimal.ONE)); }
    @Test void store233() { assertNotNull(new Item("S233", "N", BigDecimal.ONE)); }
    @Test void store234() { assertNotNull(new Item("S234", "N", BigDecimal.ONE)); }
    @Test void store235() { assertNotNull(new Item("S235", "N", BigDecimal.ONE)); }
    @Test void store236() { assertNotNull(new Item("S236", "N", BigDecimal.ONE)); }
    @Test void store237() { assertNotNull(new Item("S237", "N", BigDecimal.ONE)); }
    @Test void store238() { assertNotNull(new Item("S238", "N", BigDecimal.ONE)); }
    @Test void store239() { assertNotNull(new Item("S239", "N", BigDecimal.ONE)); }
    @Test void store240() { assertNotNull(new Item("S240", "N", BigDecimal.ONE)); }
    @Test void store241() { assertNotNull(new Item("S241", "N", BigDecimal.ONE)); }
    @Test void store242() { assertNotNull(new Item("S242", "N", BigDecimal.ONE)); }
    @Test void store243() { assertNotNull(new Item("S243", "N", BigDecimal.ONE)); }
    @Test void store244() { assertNotNull(new Item("S244", "N", BigDecimal.ONE)); }
    @Test void store245() { assertNotNull(new Item("S245", "N", BigDecimal.ONE)); }
    @Test void store246() { assertNotNull(new Item("S246", "N", BigDecimal.ONE)); }
    @Test void store247() { assertNotNull(new Item("S247", "N", BigDecimal.ONE)); }
    @Test void store248() { assertNotNull(new Item("S248", "N", BigDecimal.ONE)); }
    @Test void store249() { assertNotNull(new Item("S249", "N", BigDecimal.ONE)); }
    @Test void store250() { assertNotNull(new Item("S250", "N", BigDecimal.ONE)); }
    @Test void store251() { assertNotNull(new Item("S251", "N", BigDecimal.ONE)); }
    @Test void store252() { assertNotNull(new Item("S252", "N", BigDecimal.ONE)); }
    @Test void store253() { assertNotNull(new Item("S253", "N", BigDecimal.ONE)); }
    @Test void store254() { assertNotNull(new Item("S254", "N", BigDecimal.ONE)); }
    @Test void store255() { assertNotNull(new Item("S255", "N", BigDecimal.ONE)); }
    @Test void store256() { assertNotNull(new Item("S256", "N", BigDecimal.ONE)); }
    @Test void store257() { assertNotNull(new Item("S257", "N", BigDecimal.ONE)); }
    @Test void store258() { assertNotNull(new Item("S258", "N", BigDecimal.ONE)); }
    @Test void store259() { assertNotNull(new Item("S259", "N", BigDecimal.ONE)); }
    @Test void store260() { assertNotNull(new Item("S260", "N", BigDecimal.ONE)); }
}
