import { configureStore } from '@reduxjs/toolkit';
import metricsSlice from './metricsSlice';
import filesSlice from './filesSlice';
import type { PayloadAction } from '@reduxjs/toolkit';




const store = configureStore({
    reducer: {
        // Add reducers here
        metrics: metricsSlice,
        files: filesSlice,
    },
});

export type RootState = ReturnType<typeof store.getState>;

export default store;