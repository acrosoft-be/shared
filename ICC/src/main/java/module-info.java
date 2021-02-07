/**
 * ICC support.
 */
module be.acrosoft.gaia.shared.icc {
    exports be.acrosoft.gaia.shared.icc.drivers.beid;
    exports be.acrosoft.gaia.shared.icc;

    requires be.acrosoft.gaia.shared.dispatch;
    requires be.acrosoft.gaia.shared.util;
    requires jEidlib;
    requires java.logging;
    requires java.smartcardio;
}