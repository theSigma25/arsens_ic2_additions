package com.ic2plus.ic2plus.armor;

import com.ic2plus.ic2plus.NeutronRarity;
import com.ic2plus.ic2plus.entity.BeamAttack;
import ic2.api.item.ElectricItem;
import ic2.api.item.HudMode;
import ic2.api.item.IHazmatLike;
import ic2.api.item.IItemHudProvider;
import ic2.core.IC2;
import ic2.core.IC2Potion;
import ic2.core.init.Localization;
import ic2.core.init.MainConfig;
import ic2.core.item.ItemTinCan;
import ic2.core.item.armor.jetpack.IBoostingJetpack;
import ic2.core.ref.ItemName;
import ic2.core.util.ConfigUtil;
import ic2.core.util.StackUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.IRarity;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class NeutronStarArmor extends ElectricArmor implements IBoostingJetpack, IHazmatLike, IItemHudProvider {
    protected static final Map<Potion, Integer> potionRemovalCost = new IdentityHashMap<>();
    private final String armorName = "neutron";
    private BeamAttack beam;
    private float jumpCharge;

    public NeutronStarArmor(String registryName, EntityEquipmentSlot armorType) {
        super(registryName, "neutron_star_armor", armorType, 1.0E12F, 1.2E5F, 4);
        if (armorType == EntityEquipmentSlot.FEET) {
            MinecraftForge.EVENT_BUS.register(this);
        }

        potionRemovalCost.put(MobEffects.POISON, 10000);
        potionRemovalCost.put(IC2Potion.radiation, 10000);
        potionRemovalCost.put(MobEffects.WITHER, 25000);
    }

    private NBTTagCompound getDisplayNbt(ItemStack stack, boolean create) {
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) {
            if (!create) {
                return null;
            }

            nbt = new NBTTagCompound();
            stack.setTagCompound(nbt);
        }

        NBTTagCompound ret;
        if (!nbt.hasKey("display", 10)) {
            if (!create) {
                return null;
            }

            ret = new NBTTagCompound();
            nbt.setTag("display", ret);
        } else {
            ret = nbt.getCompoundTag("display");
        }

        return ret;
    }

    @Override
    public ISpecialArmor.ArmorProperties getProperties(EntityLivingBase entity, ItemStack armor, DamageSource source, double damage, int slot) {
        int energyPerDamage = this.getEnergyPerDamage();
        int damageLimit = ElectricItem.manager.getCharge(armor) > 1000000 ? Integer.MAX_VALUE : 100;
        if (energyPerDamage > 0) {
            damageLimit = (int) Math.min((double) damageLimit, ElectricItem.manager.getCharge(armor) / (double) energyPerDamage);
        }

        if (source == DamageSource.FALL) {
            if (this.armorType == EntityEquipmentSlot.FEET) {
                return new ISpecialArmor.ArmorProperties(10, 1.0F, damageLimit);
            }

            if (this.armorType == EntityEquipmentSlot.LEGS) {
                return new ISpecialArmor.ArmorProperties(9, 0.8, damageLimit);
            }
        }

        double absorptionRatio = this.getBaseAbsorptionRatio() * this.getDamageAbsorptionRatio();
        return new ISpecialArmor.ArmorProperties(99, absorptionRatio, damageLimit);
    }

    @SubscribeEvent(
            priority = EventPriority.LOW
    )
    public void onEntityLivingFallEvent(LivingFallEvent event) {
        if (IC2.platform.isSimulating() && event.getEntity() instanceof EntityLivingBase) {
            EntityLivingBase entity = (EntityLivingBase) event.getEntity();
            ItemStack armor = entity.getItemStackFromSlot(EntityEquipmentSlot.FEET);
            if (armor != null && armor.getItem() == this) {
                int fallDamage = Math.max((int) event.getDistance() - 50, 0);
                double energyCost = this.getEnergyPerDamage() * fallDamage;
                if (energyCost <= ElectricItem.manager.getCharge(armor)) {
                    ElectricItem.manager.discharge(armor, energyCost, Integer.MAX_VALUE, true, false, false);
                    event.setCanceled(true);
                }
            }
        }

    }

    public double getDamageAbsorptionRatio() {
        return this.armorType == EntityEquipmentSlot.CHEST ? 2.0 : 1.5;
    }

    public int getEnergyPerDamage() {
        return 50000;
    }

    @SideOnly(Side.CLIENT)
    public IRarity getForgeRarity(ItemStack stack) {
        return new NeutronRarity();
    }

    public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
        NBTTagCompound nbtData = StackUtil.getOrCreateNbtData(stack);
        byte toggleTimer = nbtData.getByte("toggleTimer");
        boolean ret = false;
        if (IC2.platform.isSimulating() && toggleTimer > 0) {
            --toggleTimer;
            nbtData.setByte("toggleTimer", toggleTimer);
        }
        switch (this.armorType) {
            case HEAD:
                int air = player.getAir();
                if (ElectricItem.manager.canUse(stack, (double) 1000.0F) && air < 100) {
                    player.setAir(air + 200);
                    ElectricItem.manager.use(stack, (double) 1000.0F, (EntityLivingBase) null);
                    ret = true;
                }
                if (ElectricItem.manager.canUse(stack, (double) 1000.0F) && player.getFoodStats().needFood()) {
                    int slot = -1;

                    for (int i = 0; i < player.inventory.mainInventory.size(); i++) {
                        ItemStack playerStack = player.inventory.mainInventory.get(i);
                        if (!StackUtil.isEmpty(playerStack) && playerStack.getItem() == ItemName.filled_tin_can.getInstance()) {
                            slot = i;
                            break;
                        }
                    }

                    if (slot > -1) {
                        ItemStack playerStack = player.inventory.mainInventory.get(slot);
                        ItemTinCan can = (ItemTinCan) playerStack.getItem();
                        ActionResult<ItemStack> result = can.onEaten(player, playerStack);
                        playerStack = result.getResult();
                        if (StackUtil.isEmpty(playerStack)) {
                            player.inventory.mainInventory.set(slot, StackUtil.emptyStack);
                        }

                        if (result.getType() == EnumActionResult.SUCCESS) {
                            ElectricItem.manager.use(stack, (double) 1000.0F, (EntityLivingBase) null);
                        }

                        ret = true;
                    }
                }

                for (PotionEffect effect : new LinkedList<PotionEffect>(player.getActivePotionEffects())) {
                    Potion potion = effect.getPotion();
                    Integer cost = potionRemovalCost.get(potion);
                    if (cost != null) {
                        cost = cost * (effect.getAmplifier() + 1);
                        if (ElectricItem.manager.canUse(stack, (double) cost)) {
                            ElectricItem.manager.use(stack, (double) cost, (EntityLivingBase) null);
                            IC2.platform.removePotion(player, potion);
                        }
                    }
                }

                boolean Nightvision = nbtData.getBoolean("Nightvision");
                short hubmode = nbtData.getShort("HudMode");
                if (IC2.keyboard.isAltKeyDown(player) && IC2.keyboard.isModeSwitchKeyDown(player) && toggleTimer == 0) {
                    toggleTimer = 10;
                    Nightvision = !Nightvision;
                    if (IC2.platform.isSimulating()) {
                        nbtData.setBoolean("Nightvision", Nightvision);
                        if (Nightvision) {
                            IC2.platform.messagePlayer(player, "Nightvision enabled.", new Object[0]);
                        } else {
                            IC2.platform.messagePlayer(player, "Nightvision disabled.", new Object[0]);
                        }
                    }
                }
                if (IC2.keyboard.isAltKeyDown(player) && IC2.keyboard.isHudModeKeyDown(player) && toggleTimer == 0) {
                    toggleTimer = 10;
                    if (hubmode == HudMode.getMaxMode()) {
                        hubmode = 0;
                    } else {
                        hubmode++;
                    }

                    if (IC2.platform.isSimulating()) {
                        nbtData.setShort("HudMode", hubmode);
                        IC2.platform.messagePlayer(player, Localization.translate(HudMode.getFromID(hubmode).getTranslationKey()), new Object[0]);
                    }
                }
                byte laserMode = 0;
                laserMode = stack.getTagCompound().getByte("laserMode");
                if (IC2.keyboard.isAltKeyDown(player) && IC2.keyboard.isSneakKeyDown(player) && toggleTimer == 0) {
                    if (laserMode == 3) laserMode = 0;
                    else laserMode++;
                    stack.getTagCompound().setByte("laserMode", laserMode);
                    IC2.platform.messagePlayer(player, "Laser mode: " + laserMode, new Object[0]);
                } else if (IC2.keyboard.isAltKeyDown(player)) {
                    if (laserMode == 0) break;
                    if (ElectricItem.manager.canUse(stack, 10000.0 * Math.pow(10, laserMode))) {
                        if (IC2.platform.isSimulating()) {
                            if (beam == null || beam.isDead) {
                                switch (laserMode) {
                                    case (1):
                                        beam = new BeamAttack(player.world, player, 128.0D, 50.0F, 8.0F, "ic2plus:textures/entity/proton_beam.png", 0);
                                        break;
                                    case (2):
                                        beam = new BeamAttack(player.world, player, 128.0D, 300.0F, 16.0F, "ic2plus:textures/entity/antimatter_beam.png", 0);
                                        break;
                                    case (3):
                                        beam = new BeamAttack(player.world, player, 128.0D, 30.0F, 0.0F, "ic2plus:textures/entity/quark_beam.png", 1);
                                        break;
                                }
                                player.world.spawnEntity(beam);
                            } else beam.renew();
                        }
                        ElectricItem.manager.use(stack, 10000.0 * Math.pow(10, laserMode), player);
                    } else {
                        if (beam != null) {
                            beam.setDead();
                            beam = null;
                        }
                    }
                } else if (beam != null) {
                    beam.setDead();
                    beam = null;
                }
                if (Nightvision && IC2.platform.isSimulating() && ElectricItem.manager.use(stack, (double) 1.0F, player)) {
                    BlockPos pos = new BlockPos((int) Math.floor(player.posX), (int) Math.floor(player.posY), (int) Math.floor(player.posZ));
                    int skylight = player.getEntityWorld().getLightFromNeighbors(pos);
                    if (skylight > 8) {
                        IC2.platform.removePotion(player, MobEffects.NIGHT_VISION);
                        player.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 100, 0, true, true));
                    } else {
                        IC2.platform.removePotion(player, MobEffects.BLINDNESS);
                        player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 300, 0, true, true));
                    }

                    ret = true;
                }
                break;
            case CHEST:
                player.extinguish();
                byte fieldPower = 0;

                fieldPower = stack.getTagCompound().getByte("fieldPower");
                if (IC2.keyboard.isSneakKeyDown(player) && IC2.keyboard.isModeSwitchKeyDown(player) && toggleTimer == 0) {
                    if (fieldPower == 3) fieldPower = 0;
                    else fieldPower++;
                    stack.getTagCompound().setByte("fieldPower", fieldPower);
                    IC2.platform.messagePlayer(player, "Field power: " + fieldPower, new Object[0]);
                }
                if (fieldPower == 0) break;
                double fieldCost = 100 * Math.pow(10, fieldPower);
                int fieldRadius;
                AxisAlignedBB field;
                List<Entity> entitiesWithinAABB;
                if (ElectricItem.manager.canUse(stack, fieldCost)) {
                    switch (fieldPower) {
                        case 1:
                            fieldRadius = 12;
                            field = new AxisAlignedBB(
                                    player.posX - fieldRadius, player.posY - fieldRadius, player.posZ - fieldRadius,
                                    player.posX + fieldRadius, player.posY + fieldRadius, player.posZ + fieldRadius
                            );
                            entitiesWithinAABB = world.getEntitiesWithinAABB(Entity.class, field);

                            for (Entity entity : entitiesWithinAABB) {
                                if (!(entity instanceof IProjectile)) continue;
                                Double dis = (double) player.getDistance(entity);
                                if (dis < 2) entity.setDead();
                                double deceleration = Math.min(1, (Math.sqrt(Math.max(dis - 3, 0))) / 3);
                                entity.motionX *= deceleration;
                                entity.motionY *= deceleration;
                                entity.motionZ *= deceleration;
                            }
                            break;
                        case (2):
                            fieldRadius = 20;
                            field = new AxisAlignedBB(
                                    player.posX - fieldRadius, player.posY - fieldRadius, player.posZ - fieldRadius,
                                    player.posX + fieldRadius, player.posY + fieldRadius, player.posZ + fieldRadius
                            );
                            entitiesWithinAABB = world.getEntitiesWithinAABB(Entity.class, field);

                            for (Entity entity : entitiesWithinAABB) {
                                if (entity == player) continue;
                                if (!(entity instanceof IProjectile || entity instanceof EntityLivingBase)) continue;

                                Vec3d dir = entity.getPositionVector().subtract(player.getPositionVector()).normalize();
                                Double dis = (double) player.getDistance(entity);
                                Double deflection = 5 / (dis * dis);
                                entity.motionX += dir.x * deflection;
                                entity.motionY += dir.y * deflection;
                                entity.motionZ += dir.z * deflection;
                                destroyEntitiy(entity, world, dis, 50, 10);
                            }
                            destroyBlocks(world, player, 16, 60);
                            break;
                        case (3):
                            fieldRadius = 50;
                            field = new AxisAlignedBB(
                                    player.posX - fieldRadius, player.posY - fieldRadius, player.posZ - fieldRadius,
                                    player.posX + fieldRadius, player.posY + fieldRadius, player.posZ + fieldRadius
                            );
                            entitiesWithinAABB = world.getEntitiesWithinAABB(Entity.class, field);

                            for (Entity entity : entitiesWithinAABB) {
                                if (entity == player) continue;
                                if (!(entity instanceof IProjectile || entity instanceof EntityLivingBase)) continue;

                                Vec3d dir = entity.getPositionVector().subtract(player.getPositionVector()).normalize();
                                Double dis = (double) player.getDistance(entity);
                                Double deflection = 25 / (dis * dis);
                                entity.motionX += dir.x * deflection;
                                entity.motionY += dir.y * deflection;
                                entity.motionZ += dir.z * deflection;
                                destroyEntitiy(entity, world, dis, 200, 20);
                            }
                            destroyBlocks(world, player, 24, 120);
                            break;
                    }
                    ElectricItem.manager.use(stack, fieldCost, null);
                    ret = true;
                }
                break;
            case LEGS:
                boolean enableQuantumSpeedOnSprint;
                if (IC2.platform.isRendering()) {
                    enableQuantumSpeedOnSprint = ConfigUtil.getBool(MainConfig.get(), "misc/quantumSpeedOnSprint");
                } else {
                    enableQuantumSpeedOnSprint = true;
                }

                if (ElectricItem.manager.canUse(stack, (double) 1000.0F) && (player.onGround || player.isInWater()) && IC2.keyboard.isForwardKeyDown(player) && (enableQuantumSpeedOnSprint && player.isSprinting() || !enableQuantumSpeedOnSprint && IC2.keyboard.isBoostKeyDown(player))) {
                    byte speedTicker = nbtData.getByte("speedTicker");
                    speedTicker++;

                    if (speedTicker >= 10) {
                        speedTicker = 0;
                        ElectricItem.manager.use(stack, (double) 1000.0F, (EntityLivingBase) null);
                        ret = true;
                    }

                    nbtData.setByte("speedTicker", speedTicker);
                    float speed = 0.22F;
                    if (player.isInWater()) {
                        speed = 0.1F;
                        if (IC2.keyboard.isJumpKeyDown(player)) {
                            player.motionY += 0.1F;
                        }
                    }

                    if (speed > 0.0F) {
                        player.moveRelative(0.0F, 0.0F, 1.0F, speed);
                    }
                }

                break;
            case FEET:
                if (IC2.platform.isSimulating()) {
                    boolean wasOnGround = !nbtData.hasKey("wasOnGround") || nbtData.getBoolean("wasOnGround");
                    if (wasOnGround && !player.onGround && IC2.keyboard.isJumpKeyDown(player) && IC2.keyboard.isBoostKeyDown(player)) {
                        ElectricItem.manager.use(stack, (double) 4000.0F, (EntityLivingBase) null);
                        ret = true;
                    }

                    if (player.onGround != wasOnGround) {
                        nbtData.setBoolean("wasOnGround", player.onGround);
                    }
                } else {
                    if (ElectricItem.manager.canUse(stack, (double) 4000.0F) && player.onGround) {
                        this.jumpCharge = 1.0F;
                    }

                    if (player.motionY >= (double) 0.0F && this.jumpCharge > 0.0F && !player.isInWater()) {
                        if (IC2.keyboard.isJumpKeyDown(player) && IC2.keyboard.isBoostKeyDown(player)) {
                            if (this.jumpCharge == 1.0F) {
                                player.motionX *= 7F;
                                player.motionZ *= 7F;
                            }

                            player.motionY += this.jumpCharge * 0.5F;
                            this.jumpCharge = (float) ((double) this.jumpCharge * (double) 0.75F);
                        } else if (this.jumpCharge < 1.0F) {
                            this.jumpCharge = 0.0F;
                        }
                    }
                }
        }
        if (ret) {
            player.inventoryContainer.detectAndSendChanges();
        }
    }

    void destroyBlocks(World world, EntityPlayer player, int size, double power) {
        if (world.getTotalWorldTime() % 20 == 0) {
            for (int x = -size; x <= size; x++) {
                for (int y = -size; y <= size; y++) {
                    for (int z = -size; z <= size; z++) {

                        BlockPos pos = new BlockPos(
                                player.posX + x,
                                player.posY + y,
                                player.posZ + z
                        );

                        if (world.isAirBlock(pos))
                            continue;

                        double distance = player.getDistance(
                                pos.getX(),
                                pos.getY(),
                                pos.getZ()
                        );

                        if (distance < 1)
                            distance = 1;

                        float resistance = world.getBlockState(pos).getBlock().getExplosionResistance(null);

                        if (power / (distance * distance) > resistance) {
                            world.destroyBlock(pos, true);
                        }
                    }
                }
            }
        }
    }

    void destroyEntitiy(Entity entity, World world, double dis, double damagePower, double r) {
        if (entity instanceof EntityLivingBase && world.getTotalWorldTime() % 10 == 0 && dis <= r) {
            float damage = (float) (damagePower / (dis * dis));
            entity = entity;
            entity.attackEntityFrom(DamageSource.FLY_INTO_WALL, damage);
        }
    }

    @Override
    public boolean addsProtection(EntityLivingBase entity, EntityEquipmentSlot slot, ItemStack stack) {
        return ElectricItem.manager.getCharge(stack) > (double) 0.0F;
    }


    @Override
    public boolean drainEnergy(ItemStack pack, int amount) {
        return ElectricItem.manager.discharge(pack, (double) (amount * 10), Integer.MAX_VALUE, true, false, false) > (double) 0.0F;
    }

    @Override
    public float getPower(ItemStack stack) {
        return 3.0F;
    }

    @Override
    public float getDropPercentage(ItemStack stack) {
        return 0.001F;
    }

    @Override
    public double getChargeLevel(ItemStack stack) {
        return ElectricItem.manager.getCharge(stack) / this.getMaxCharge(stack);
    }

    @Override
    public boolean isJetpackActive(ItemStack stack) {
        return true;
    }

    @Override
    public float getHoverMultiplier(ItemStack stack, boolean upwards) {
        return 0.5F;
    }

    @Override
    public float getWorldHeightDivisor(ItemStack stack) {
        return 1.0F;
    }

    @Override
    public float getBaseThrust(ItemStack stack, boolean hover) {
        return hover ? 1.0F : 0.5F;
    }

    @Override
    public float getBoostThrust(EntityPlayer player, ItemStack stack, boolean hover) {
        return IC2.keyboard.isBoostKeyDown(player) && ElectricItem.manager.getCharge(stack) >= (double) 834.0F ? (hover ? 0.1F : 0.5F) : 0.0F;
    }

    @Override
    public boolean useBoostPower(ItemStack stack, float hover) {
        return ElectricItem.manager.discharge(stack, (double) 1000.0F, Integer.MAX_VALUE, true, false, false) > (double) 0.0F;
    }

    @Override
    public float getHoverBoost(EntityPlayer player, ItemStack stack, boolean boostAmount) {
        if (IC2.keyboard.isBoostKeyDown(player) && ElectricItem.manager.getCharge(stack) >= (double) 834.0F) {
            if (!player.onGround) {
                ElectricItem.manager.discharge(stack, (double) 834.0F, Integer.MAX_VALUE, true, false, false);
            }

            return 3.0F;
        } else {
            return 1.0F;
        }
    }


    public boolean doesProvideHUD(ItemStack stack) {
        return this.armorType == EntityEquipmentSlot.HEAD && ElectricItem.manager.getCharge(stack) > (double) 0.0F;
    }

    public HudMode getHudMode(ItemStack stack) {
        return HudMode.getFromID(StackUtil.getOrCreateNbtData(stack).getShort("HudMode"));
    }
}
