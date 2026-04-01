/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,jsx}'],
  theme: {
    extend: {
      colors: {
        ink: '#0f172a',
        skyline: '#0ea5e9',
        lime: '#65a30d',
        coral: '#fb7185'
      }
    }
  },
  plugins: []
};
