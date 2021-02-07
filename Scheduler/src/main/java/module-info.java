/**
 * System schedule (CRON or Task manager) support.
 */
module be.acrosoft.gaia.shared.scheduler {
    exports be.acrosoft.gaia.shared.scheduler;

    requires transitive be.acrosoft.gaia.shared.util;
    requires java.logging;
}