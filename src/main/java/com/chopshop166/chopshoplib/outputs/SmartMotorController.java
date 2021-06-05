package com.chopshop166.chopshoplib.outputs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.chopshop166.chopshoplib.sensors.IEncoder;
import com.chopshop166.chopshoplib.sensors.MockEncoder;

import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;

/**
 * A SpeedController with features attached.
 * 
 * This is used in a few select scenarios, when we want to keep the exact type
 * of an object general, but at the same time want to access it as one of two
 * disconnected interfaces.
 */
public class SmartMotorController implements Sendable, SpeedController {

    /** The wrapped motor controller, as a sendable object. */
    private final Sendable sendable;
    /** The wrapped motor controller. */
    private final SpeedController wrapped;
    /** An encoder, if one is attached and supplied. */
    private final IEncoder encoder;
    /** All the modifiers to apply to the speed controller. */
    final private List<Modifier> modifiers;

    /** Construct with mocks for everything */
    public SmartMotorController() {
        this(new MockSpeedController(), new MockEncoder());
    }

    /**
     * Wrap a motor controller.
     * 
     * @param <T>       The base type to wrap
     * @param wrapped   The wrapped motor controller.
     * @param modifiers Any output modifiers.
     */
    public <T extends Sendable & SpeedController> SmartMotorController(final T wrapped, final Modifier... modifiers) {
        this(wrapped, new MockEncoder(), modifiers);
    }

    /**
     * Wrap a motor controller with an encoder.
     * 
     * @param <T>       The base type to wrap
     * @param wrapped   The wrapped motor controller.
     * @param encoder   The encoder attached to the motor controller.
     * @param modifiers Any output modifiers.
     */
    public <T extends Sendable & SpeedController> SmartMotorController(final T wrapped, final IEncoder encoder,
            final Modifier... modifiers) {
        sendable = wrapped;
        this.wrapped = wrapped;
        this.encoder = encoder;
        this.modifiers = new ArrayList<>(Arrays.asList(modifiers));
    }

    /**
     * Get the encoder attached to the robot.
     * 
     * @return An encoder, or a mock if none is attached.
     */
    public IEncoder getEncoder() {
        return encoder;
    }

    /**
     * Add modifiers to the speed controller.
     * 
     * @param m  First modifier.
     * @param ms Any extra modifiers (optional).
     */
    public void addModifiers(final Modifier m, final Modifier... ms) {
        modifiers.add(m);
        modifiers.addAll(Arrays.asList(ms));
    }

    /**
     * Add all modifiers from a collection.
     * 
     * @param ms Collection of modifiers.
     */
    public void addAllModifiers(final Collection<? extends Modifier> ms) {
        modifiers.addAll(ms);
    }

    /**
     * Set the setpoint.
     * 
     * @param setPoint The new setpoint.
     */
    public void setSetpoint(final double setPoint) {
        // Do nothing for this class
    }

    @Override
    public void set(final double speed) {
        wrapped.set(calculateModifiers(speed));
    }

    @Override
    public double get() {
        return wrapped.get();
    }

    /**
     * Warning: Do not use in a subsystem.
     * 
     * This is intended for configuration in the map only, but the SpeedController
     * requires it to exist.
     */
    @Override
    public void setInverted(final boolean isInverted) {
        wrapped.setInverted(isInverted);
    }

    @Override
    public boolean getInverted() {
        return wrapped.getInverted();
    }

    @Override
    public void disable() {
        wrapped.disable();
    }

    @Override
    public void stopMotor() {
        wrapped.stopMotor();
    }

    /** TODO: Remove when WPIlib does. */
    @Override
    public void pidWrite(final double output) {
        set(output);
    }

    @Override
    public void initSendable(final SendableBuilder builder) {
        sendable.initSendable(builder);
    }

    /**
     * Run all modifiers.
     * 
     * As modifiers could have side effects, this is private.
     * 
     * @param rawSpeed The base speed to run
     * @return The new speed
     */
    private double calculateModifiers(final double rawSpeed) {
        double speed = rawSpeed;
        for (final Modifier m : modifiers) {
            speed = m.applyAsDouble(speed);
        }
        return speed;
    }
}
