export default function LoadingSpinner({ size = 'md' }) {
  const s = size === 'sm' ? 'w-4 h-4' : size === 'lg' ? 'w-10 h-10' : 'w-7 h-7'
  return (
    <div className="flex justify-center items-center py-8">
      <div className={`${s} border-2 border-slate-700 border-t-brand-500 rounded-full animate-spin`} />
    </div>
  )
}
