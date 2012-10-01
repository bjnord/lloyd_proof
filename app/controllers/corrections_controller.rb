class CorrectionsController < ApplicationController
  respond_to :html, :except => :sync

  def index
    respond_with(@corrections = Correction.all)
  end

  def sync
    @statuses = params[:corrections].collect {|c| sync_one(c) }
    respond_to do |format|
      format.json { render :json => @statuses }
    end
  end

protected

  def sync_one(correction_hash)
    sync_id = correction_hash.delete('sync_id')
    begin
      corr = Correction.new(correction_hash)
      return {:sync_id => sync_id, :status => :ok} if corr.save
      return {:sync_id => sync_id, :status => :error, :errors => corr.errors.full_messages}
    rescue ActiveModel::MassAssignmentSecurity::Error => e
      return {:sync_id => sync_id, :status => :error, :errors => [e.message]}
    end
  end
end
