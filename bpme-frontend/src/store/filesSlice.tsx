import { createSlice } from '@reduxjs/toolkit';
import type { PayloadAction } from '@reduxjs/toolkit';



interface FilesState {
    files: File[];
}

const initialState = { files: [] } as FilesState;


const filesSlice = createSlice({
    name: 'files',
    initialState,
    reducers: {
        // Add reducers here
        addFiles: (state, action: PayloadAction<File[]>) => {
            state.files = state.files.concat(action.payload)
        },
        removeAllFiles: (state) => {
            state.files = []
        },
        removeSpecificFile: (state, action: PayloadAction<string>) => {
            state.files = state.files.filter(file => file.name !== action.payload)
        },
    },
});

export const { addFiles, removeAllFiles, removeSpecificFile } = filesSlice.actions;

export default filesSlice.reducer;