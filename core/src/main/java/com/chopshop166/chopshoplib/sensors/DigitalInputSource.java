package com.chopshop166.chopshoplib.sensors;

import java.util.function.BooleanSupplier;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;

/**
 * A {@link DigitalInput} that acts as a {@link BooleanSupplier}.
 */
@FunctionalInterface
public interface DigitalInputSource extends Sendable, BooleanSupplier {

    @Override
    default void initSendable(final SendableBuilder builder) {
        builder.setSmartDashboardType("Digital Input");
        builder.addBooleanProperty("Value", this::getAsBoolean, null);
    }
}