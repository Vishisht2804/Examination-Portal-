export default function LoadingSpinner() {
  return (
    <div className="flex items-center justify-center py-16">
      <div className="h-10 w-10 animate-spin rounded-full border-4 border-sky-200 border-t-sky-600" />
    </div>
  );
}
