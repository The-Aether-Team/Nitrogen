package com.aetherteam.nitrogen.fabric;

import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Iterator;
import java.util.List;

public class WrappedInventoryStorage implements InventoryStorage {

    private final InventoryStorage storage;

    public WrappedInventoryStorage(InventoryStorage storage) {
        this.storage = storage;
    }

    @Override
    public @UnmodifiableView List<SingleSlotStorage<ItemVariant>> getSlots() {
        return this.storage.getSlots();
    }

    @Override
    public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        return this.storage.insert(resource, maxAmount, transaction);
    }

    @Override
    public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        return this.storage.insert(resource, maxAmount, transaction);
    }

    @Override
    public Iterator<StorageView<ItemVariant>> iterator() {
        return this.storage.iterator();
    }
}
