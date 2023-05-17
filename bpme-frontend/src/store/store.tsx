import { configureStore } from '@reduxjs/toolkit';
import metricsSlice from './metricsSlice';
import filesSlice from './filesSlice';
import type { PayloadAction } from '@reduxjs/toolkit';
import { analysisApi } from '../api/analysis';



const store = configureStore({
    reducer: {
        // Add reducers here
        metrics: metricsSlice,
        files: filesSlice,
        [analysisApi.reducerPath]: analysisApi.reducer,
    },
    middleware: (getDefaultMiddleware) =>
        getDefaultMiddleware().concat(analysisApi.middleware),
});

export type RootState = ReturnType<typeof store.getState>;

export default store;