package com.ic2plus.ic2plus.entity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class NeutronStar extends Entity {
    private float radius = 0.01F;
    private float mass = 0.01F;
    private float temperature= 1.0F;
    private float electricField= 1.0F;
    private float magneticField=1.0F;
    private float rotationSpeed=0.01F;

    public NeutronStar(World world) {
        super(world);
        noClip = true;
        this.ignoreFrustumCheck = true;
        this.setEntityBoundingBox(new AxisAlignedBB(
                posX - 128.0, posY - 128.0, posZ - 128.0,
                posX + 128.0, posY + 128.0, posZ + 128.0
        ));
        setSize(radius * 10, radius * 10);
    }
    public NeutronStar(World world, double x, double y, double z) {
        this(world);
        this.setPosition(x, y, z);
    }
    @Override
    public void onUpdate() {
        super.onUpdate();
        setSize(radius * 10, radius * 10);
        this.prevRotationYaw = this.rotationYaw;
        this.prevRotationPitch = this.rotationPitch;

        if (!world.isRemote && this.ticksExisted >= 6000)
            setDead();

        this.rotationYaw = (this.ticksExisted * 0.1F) % 360.0F;
        this.rotationPitch = (this.ticksExisted * 0.1F) % 360.0F;

        if (!world.isRemote) {
            pullEntities();
            damageEntities();
            destroyBlocks();
            createLightning();
            jetDestroy();
        }
    }
    private void damageEntities() {
        for(Entity entity : world.getEntitiesWithinAABB(Entity.class, getEntityBoundingBox())) {
            if (entity == this || (ownerUUID != null && entity.getUniqueID().equals(ownerUUID))) continue;
            entity.attackEntityFrom(
                    DamageSource.CACTUS,
                    100*mass
            );
            if (!(entity instanceof EntityLivingBase)){
                entity.setDead();
            }
        }
    }
    private void pullEntities(){
        AxisAlignedBB box = getEntityBoundingBox().grow(128);

        for(Entity entity : world.getEntitiesWithinAABB(Entity.class, box)){

            if(entity == this || (entity instanceof EntityPlayer && ((EntityPlayer) entity).isCreative()) || (ownerUUID != null && entity.getUniqueID().equals(ownerUUID)))
                continue;


            Vec3d dir = new Vec3d(posX, posY, posZ)
                    .subtract(entity.getPositionVector());

            double distance = dir.lengthVector();

            dir = dir.normalize();
            double force = Math.min(5 * mass  / distance ,2 * mass);
            Vec3d tangent = new Vec3d(-dir.z, 0, dir.x);
            double k = 0.1 + (0.9 / (1.0 + Math.exp(0.5 * (distance - (50.0*rotationSpeed)))));
            double swirl = Math.min(4.0*rotationSpeed/distance*k, 0.15*rotationSpeed);

            entity.motionX += dir.x * force + tangent.x * swirl;
            entity.motionY += dir.y * force;
            entity.motionZ += dir.z * force + tangent.z * swirl;

            entity.velocityChanged = true;
        }
    }
    private void destroyBlocks(){
        if (world.getTotalWorldTime() % 20 != 0) return;
        int size= (int) (50*mass);
        for (int x = -size; x <= size; x++) {
            for (int y = -size; y <= size; y++) {
                for (int z = -size; z <= size; z++) {

                    BlockPos pos = new BlockPos(
                            this.posX + x,
                            this.posY + y,
                            this.posZ + z
                    );

                    if (world.isAirBlock(pos))
                        continue;

                    double distance = this.getDistance(
                            pos.getX(),
                            pos.getY(),
                            pos.getZ()
                    );
                    if (distance < 25*mass){
                        world.destroyBlock(pos,false);
                    }else if(distance < 50*mass && world.getBlockState(pos).getBlock().getExplosionResistance(null)<7 && rand.nextDouble()<0.1){
                        world.destroyBlock(pos,false);
                    }
                }
            }
        }
    }

    private void createLightning(){
        if(world.getTotalWorldTime()%5 != 0) return;
        if  (rand.nextDouble()>0.1*this.electricField) return;
        int x = (int)this.posX + rand.nextInt(257)-128;
        int z = (int)this.posZ + rand.nextInt(257)-128;
        BlockPos pos = world.getHeight(new BlockPos(x, 0, z));
        world.addWeatherEffect(
            new EntityLightningBolt(
                    world,
                    pos.getX(), pos.getY(), pos.getZ(),
                    false
            )
        );
        world.createExplosion(null, pos.getX(),pos.getY(),pos.getZ(),this.electricField*5,true);
    }
    private void jetDestroy() {
        if (world.isRemote || ticksExisted % 5 != 0 || this.ticksExisted < 40) return;

        double radius = 5.0*this.radius;
        double rayLength = 120.0*this.radius;

        Vec3d start = new Vec3d(posX, posY, posZ);

        for (int dir : new int[]{1, -1}) {
            float yawRad = (float) Math.toRadians(rotationYaw);
            float pitchRad = (float) Math.toRadians(rotationPitch);

            double x = dir * Math.sin(pitchRad) * Math.sin(yawRad);
            double y = dir * Math.cos(pitchRad);
            double z = dir * Math.sin(pitchRad) * Math.cos(yawRad);

            Vec3d mainDir = new Vec3d(x, y, z).normalize();
            Vec3d end = start.add(mainDir.scale(rayLength));

            Vec3d rayDir = end.subtract(start);
            double lenSq = rayDir.lengthSquared();

            if (lenSq == 0.0) continue;

            AxisAlignedBB box = new AxisAlignedBB(start, end).grow(radius);

            Set<BlockPos> destroyed = new HashSet<>();
            BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

            for (int bx = (int) Math.floor(box.minX); bx <= (int) Math.floor(box.maxX); bx++) {
                for (int by = (int) Math.floor(box.minY); by <= (int) Math.floor(box.maxY); by++) {
                    for (int bz = (int) Math.floor(box.minZ); bz <= (int) Math.floor(box.maxZ); bz++) {
                        pos.setPos(bx, by, bz);
                        if (world.isAirBlock(pos)) continue;
                        Vec3d p = new Vec3d(bx + 0.5, by + 0.5, bz + 0.5);

                        double t = p.subtract(start).dotProduct(rayDir) / lenSq;
                        t = Math.max(0.0, Math.min(1.0, t));

                        Vec3d closest = start.add(rayDir.scale(t));

                        if (closest.squareDistanceTo(p) <= radius * radius) {
                            BlockPos immutablePos = pos.toImmutable();
                            IBlockState state = world.getBlockState(immutablePos);
                            if (state.getBlockHardness(world, immutablePos) >= 0) {
                                destroyed.add(immutablePos);
                            }
                        }
                    }
                }
            }
            for (BlockPos block : destroyed) {
                world.setBlockToAir(block);
            }
        }
    }
    private UUID ownerUUID;

    public void setOwner(EntityPlayer player) {
        if (player != null) {
            this.ownerUUID = player.getUniqueID();
        }
    }
    @Override
    public int getBrightnessForRender() {
        return 15728880;
    }
    @Override
    public float getBrightness() {
        return 1.0F;
    }

    @Override
    protected void entityInit() {}

    public float getRadius() {
        return radius;
    }
    public float getTemperature() {
        return temperature;
    }
    public float getElectricField() {
        return electricField;
    }
    public float getMagneticField() {
        return magneticField;
    }
    public float getMass() {
        return mass;
    }
    public float getRotationSpeed() {
        return rotationSpeed;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean isInRangeToRenderDist(double distance) {
        return true;
    }
    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return this.getEntityBoundingBox().grow(128);
    }
    @Override
    @SideOnly(Side.CLIENT)
    public boolean isInRangeToRender3d(double x, double y, double z) {
        return true;
    }


    @Override
    protected void readEntityFromNBT(NBTTagCompound nbt) {}

    @Override
    protected void writeEntityToNBT(NBTTagCompound nbt) {}

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }
}