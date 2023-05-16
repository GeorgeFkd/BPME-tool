import { createSlice } from '@reduxjs/toolkit';
import type { PayloadAction } from '@reduxjs/toolkit';
import { ALL_METRICS } from '../constants/metrics';
interface MetricsState {
    metrics: string[];
}

const initialState = { metrics: [] } as MetricsState;

const metricsSlice = createSlice({
    name: 'metrics',
    initialState,
    reducers: {
        // Add reducers here
        addMetric: (state, action: PayloadAction<string>) => {
            console.log(state);
            state.metrics.push(action.payload)
        },
        removeMetric: (state, action) => {
            state.metrics = state.metrics.filter(metric => metric !== action.payload)
        },
        addMultipleMetrics: (state, action: PayloadAction<string[]>) => {
            state.metrics = state.metrics.concat(action.payload)
        },
        addAllMetrics: (state) => {
            console.log(state)
            state.metrics = ALL_METRICS;
        },
        removeAllMetrics: (state) => {
            state.metrics = []
        },
        toggleMetric: (state, action: PayloadAction<string>) => {
            if (state.metrics.includes(action.payload)) {
                state.metrics = state.metrics.filter(metric => metric !== action.payload)
            } else {
                state.metrics.push(action.payload)
            }
        }

    },
});

export const { addMetric, removeMetric, addAllMetrics, addMultipleMetrics, removeAllMetrics, toggleMetric } = metricsSlice.actions;
export default metricsSlice.reducer;