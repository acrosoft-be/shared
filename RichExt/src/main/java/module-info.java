/**
 * Extensions for Rich support.
 */
module be.acrosoft.gaia.shared.rich.ext {
    exports be.acrosoft.gaia.shared.rich.ext.writers;
    exports be.acrosoft.gaia.shared.rich.ext.readers;

    requires transitive be.acrosoft.gaia.shared.rich;
    requires org.apache.poi.ooxml;
    requires org.apache.poi.ooxml.schemas;
    requires org.apache.poi.poi;
    requires org.apache.poi.scratchpad;
    requires org.apache.xmlbeans;
    requires transitive org.eclipse.swt.win32.win32.x86;
}