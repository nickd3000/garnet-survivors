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
    public ParticleTemplate acidRing;
    public ParticleTemplate acid;
    public ParticleTemplate ice;
    public ParticleTemplate blood;

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

        acidRing = new ParticleTemplate();
        acidRing.setLifeTime(0.2, 1.5);
        acidRing.setSpeed(1, 1);
        acidRing.setPositionJitter(1.1);
        acidRing.setColorSupplier(new ColorSupplierLinear(new int[]{ColorUtils.asRGBA(0.3f, 1, 0, 0.8f), ColorUtils.asRGBA(0.3f, 1, 0, 0)}));
        acidRing.setSpeedCurve(new StandardCurve(CurveType.LINE_DOWN));
        acidRing.setParticleDrawer(p -> {
            int col = p.colorSupplier.getColor(p.getTime());
            spriteHelper.drawSpriteInMap((int) p.position.x, (int) p.position.y, 0, 2, 0, col);
        });
        acid = new ParticleTemplate();
        acid.setLifeTime(0.2, 3.5);
        acid.setSpeed(5, 5);
        acid.setPositionJitter(2.1);
        acid.setColorSupplier(new ColorSupplierLinear(new int[]{ColorUtils.asRGBA(0.8f, 1, 0, 0.8f), ColorUtils.asRGBA(0.0f, 0, 1, 0)}));
        acid.setSpeedCurve(new StandardCurve(CurveType.LINE_DOWN));
        acid.setParticleDrawer(p -> {
            int col = p.colorSupplier.getColor(p.getTime());
            spriteHelper.drawSpriteInMap((int) p.position.x, (int) p.position.y, 11, 1, 0, col);
        });

        ice = new ParticleTemplate();
        ice.setLifeTime(0.2, 3.5);
        ice.setSpeed(5, 5);
        ice.setPositionJitter(2.1);
        ice.setColorSupplier(new ColorSupplierLinear(new int[]{ColorUtils.asRGBA(0.3f, 0.5f, 1, 0.8f), ColorUtils.asRGBA(0.0f, 0, 1, 0)}));
        ice.setSpeedCurve(new StandardCurve(CurveType.LINE_DOWN));
        ice.setParticleDrawer(p -> {
            int col = p.colorSupplier.getColor(p.getTime());
            spriteHelper.drawSpriteInMap((int) p.position.x, (int) p.position.y, 11, 1, 0, col);
        });

        blood = new ParticleTemplate();
        blood.setLifeTime(0.2, 1.0);
        blood.setSpeed(10, 50);
        blood.setPositionJitter(1.1);
        blood.setColorSupplier(new ColorSupplierLinear(new int[]{ColorUtils.asRGBA(1, 0, 0, 0.9f), ColorUtils.asRGBA(1, 0, 0, 0)}));
        blood.setSpeedCurve(new StandardCurve(CurveType.LINE_DOWN));
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
