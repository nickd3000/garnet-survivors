package com.physmo.survivor.components;

import com.physmo.garnet.ColorUtils;
import com.physmo.garnet.graphics.Graphics;
import com.physmo.garnet.structure.Vector3;
import com.physmo.garnet.toolkit.Component;
import com.physmo.garnet.toolkit.color.ColorSupplierLinear;
import com.physmo.garnet.toolkit.curve.CurveType;
import com.physmo.garnet.toolkit.curve.StandardCurve;
import com.physmo.garnet.toolkit.particle.Particle;
import com.physmo.garnet.toolkit.particle.ParticleManager;
import com.physmo.garnet.toolkit.particle.ParticleTemplate;

public class ParticleFactory extends Component {

    public ParticleTemplate wandTrail;
    public ParticleTemplate glaveTrail;
    public ParticleTemplate lightSmoke;
    public ParticleTemplate flame;

    ParticleManager particleManager;
    SpriteHelper spriteHelper;

    @Override
    public void init() {
        particleManager = getObjectByTypeFromParentContext(ParticleManager.class);
        spriteHelper = getComponentFromParentContext(SpriteHelper.class);

        wandTrail = new ParticleTemplate();
        wandTrail.setLifeTime(0.2, 1.0);
        wandTrail.setSpeed(10, 50);
        wandTrail.setPositionJitter(1.1);
        wandTrail.setColorSupplier(new ColorSupplierLinear(new int[]{ColorUtils.asRGBA(1, 0, 1, 0.9f), ColorUtils.asRGBA(0, 1, 1, 0)}));
        wandTrail.setSpeedCurve(new StandardCurve(CurveType.LINE_DOWN));

        ColorSupplierLinear glaveColor = new ColorSupplierLinear(
                new int[]{
                        0xffffff70,
                        0xff1c2600});

        glaveTrail = new ParticleTemplate();
        glaveTrail.setLifeTime(0.2, 0.5);
        glaveTrail.setSpeed(0, 5);
        glaveTrail.setPositionJitter(1.1);
        glaveTrail.setColorSupplier(glaveColor);
        glaveTrail.setSpeedCurve(new StandardCurve(CurveType.LINE_DOWN));
        glaveTrail.setParticleDrawer(p -> {
            int col = p.colorSupplier.getColor(p.getTime());
            spriteHelper.drawSpriteInMap((int) p.position.x, (int) p.position.y, 4, 2, 0, col);
        });

        lightSmoke = new ParticleTemplate();
        lightSmoke.setLifeTime(2.2, 3.5);
        lightSmoke.setSpeed(10, 10);
        lightSmoke.setPositionJitter(3.1);
        lightSmoke.setColorSupplier(new ColorSupplierLinear(new int[]{ColorUtils.asRGBA(1, 1, 1, 0), ColorUtils.asRGBA(1, 1, 1, 0.8f), ColorUtils.asRGBA(1, 1, 1, 0)}));
        lightSmoke.setSpeedCurve(new StandardCurve(CurveType.LINE_DOWN));
        lightSmoke.setParticleDrawer(p -> {
            int col = p.colorSupplier.getColor(p.getTime());
            spriteHelper.drawSpriteInMap((int) p.position.x, (int) p.position.y, 11, 1, 0, col);
        });

        flame = new ParticleTemplate();
        flame.setLifeTime(0.2, 1.5);
        flame.setSpeed(10, 10);
        flame.setPositionJitter(1.1);
        flame.setColorSupplier(new ColorSupplierLinear(new int[]{ColorUtils.asRGBA(1, 1, 1, 0.8f), ColorUtils.asRGBA(1, 0, 0, 0)}));
        flame.setSpeedCurve(new StandardCurve(CurveType.LINE_DOWN));
        flame.setParticleDrawer(p -> {
            int col = p.colorSupplier.getColor(p.getTime());
            spriteHelper.drawSpriteInMap((int) p.position.x, (int) p.position.y, 0, 2, 0, col);
        });
    }

    public void createParticle(ParticleTemplate template, Vector3 position) {
        Particle freeParticle = particleManager.getFreeParticle();
        if (freeParticle != null) {
            template.initParticle(freeParticle, position);
        }
    }

    @Override
    public void tick(double t) {

    }

    @Override
    public void draw(Graphics g) {

    }
}
